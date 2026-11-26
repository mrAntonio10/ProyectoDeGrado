package com.upb.cores.rekids.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upb.models.product.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RekidsExternalPortalClient implements RekidsExternalPortalFeignClient {

    public static final String BRANCH_LA_PAZ_ID = "10000000-0000-4000-8000-000000000001";
    public static final String BRANCH_SANTA_CRUZ_ID = "10000000-0000-4000-8000-000000000002";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${REKIDS_API_TOKEN:}")
    private String configuredToken;

    @Value("${rekids.external.portal.url:https://testendpoints.onrender.com}")
    private String externalPortalUrl;

    public RekidsExternalPortalClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(8000);
        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene las sucursales autorizadas exclusivamente desde el gateway HTTP.
     */
    @Override
    public RekidsSandboxBranchListResponseDto listBranches() {
        ResponseEntity<RekidsSandboxBranchListResponseDto> resp = restTemplate.exchange(
                normalizeBaseUrl(externalPortalUrl) + "/integrations/v1/branches",
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                RekidsSandboxBranchListResponseDto.class);
        RekidsSandboxBranchListResponseDto response = resp.getBody();
        if (response == null || response.getItems() == null) {
            throw new IllegalStateException("El gateway ReKids devolvió una respuesta de sucursales inválida.");
        }
        response.getItems().forEach(branch ->
                branch.setRegion(resolveRegionFromBranch(branch.getId(), branch.getName())));
        return response;
    }

    /**
     * Consulta una página del catálogo comercial de una sucursal:
     * GET /integrations/v1/products?branch_id=UUID&cursor=...&limit=...
     */
    @Override
    public RekidsSandboxProductPageResponseDto listProductsByBranch(String branchId, String cursor, Integer limit) {
        int safeLimit = (limit != null && limit >= 1 && limit <= 100) ? limit : 50;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(normalizeBaseUrl(externalPortalUrl) + "/integrations/v1/products")
                .queryParam("branch_id", branchId)
                .queryParam("limit", safeLimit);
        if (cursor != null && !cursor.isBlank()) uriBuilder.queryParam("cursor", cursor);
        URI uri = uriBuilder.build().encode().toUri();
        ResponseEntity<RekidsSandboxProductPageResponseDto> resp = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(buildAuthHeaders()),
                RekidsSandboxProductPageResponseDto.class);
        RekidsSandboxProductPageResponseDto response = resp.getBody();
        if (response == null || response.getItems() == null) {
            throw new IllegalStateException("El gateway ReKids devolvió una página de productos inválida.");
        }
        return enrichPageResponseMetadata(response, branchId, safeLimit, 0);
    }

    @Override
    public RekidsSandboxProductPageResponseDto listProductsByBranchFiltered(
            String branchId,
            String cursor,
            Integer limit,
            Integer page,
            String filter,
            String category,
            String size,
            String brand,
            String condition,
            String availability
    ) {
        int safeLimit = (limit != null && limit >= 1 && limit <= 100) ? limit : 50;
        int safePage = (page != null && page >= 0) ? page : 0;

        boolean hasCustomFilters = (filter != null && !filter.isBlank())
                || (category != null && !category.isBlank() && !category.equalsIgnoreCase("TODOS") && !category.equalsIgnoreCase("Todas"))
                || (size != null && !size.isBlank() && !size.equalsIgnoreCase("TODOS") && !size.equalsIgnoreCase("Todas"))
                || (brand != null && !brand.isBlank() && !brand.equalsIgnoreCase("TODOS") && !brand.equalsIgnoreCase("Todas"))
                || (condition != null && !condition.isBlank() && !condition.equalsIgnoreCase("TODOS") && !condition.equalsIgnoreCase("Todas"))
                || (availability != null && !availability.isBlank() && !availability.equalsIgnoreCase("TODOS") && !availability.equalsIgnoreCase("Todas"));

        // Si se usa cursor directo sin filtros adicionales ni número de página > 0, consumir página directa del tercero
        if (!hasCustomFilters && (page == null || page == 0) && (cursor != null && !cursor.isBlank())) {
            return listProductsByBranch(branchId, cursor, safeLimit);
        }

        // Recorrer páginas del tercero usando next_cursor (respetando el contrato de /integrations/v1/products)
        List<RekidsSandboxProductDto> allBranchProducts = fetchAllProductsForBranch(branchId);
        int totalAvailableInBranch = (int) allBranchProducts.stream()
                .filter(p -> "available".equalsIgnoreCase(p.getAvailability()))
                .count();
        int totalUnavailableInBranch = allBranchProducts.size() - totalAvailableInBranch;

        String afterUuid = decodeAfterFromCursor(cursor, branchId);

        List<RekidsSandboxProductDto> filtered = allBranchProducts.stream()
                .filter(p -> {
                    if (category != null && !category.isBlank() && !category.equalsIgnoreCase("TODOS") && !category.equalsIgnoreCase("Todas")) {
                        if (p.getCategory() == null || !p.getCategory().toLowerCase().contains(category.trim().toLowerCase())) {
                            return false;
                        }
                    }
                    if (size != null && !size.isBlank() && !size.equalsIgnoreCase("TODOS") && !size.equalsIgnoreCase("Todas")) {
                        if (p.getSize() == null || !p.getSize().equalsIgnoreCase(size.trim())) {
                            return false;
                        }
                    }
                    if (brand != null && !brand.isBlank() && !brand.equalsIgnoreCase("TODOS") && !brand.equalsIgnoreCase("Todas")) {
                        String pBrand = p.getBrand() != null ? p.getBrand() : "ReKids";
                        if (!pBrand.toLowerCase().contains(brand.trim().toLowerCase())) {
                            return false;
                        }
                    }
                    if (condition != null && !condition.isBlank() && !condition.equalsIgnoreCase("TODOS") && !condition.equalsIgnoreCase("Todas")) {
                        if (p.getCondition() == null || !p.getCondition().toLowerCase().contains(condition.trim().toLowerCase())) {
                            return false;
                        }
                    }
                    if (availability != null && !availability.isBlank() && !availability.equalsIgnoreCase("TODOS") && !availability.equalsIgnoreCase("Todas")) {
                        if (p.getAvailability() == null || !p.getAvailability().equalsIgnoreCase(availability.trim())) {
                            return false;
                        }
                    }
                    if (filter != null && !filter.isBlank()) {
                        String q = filter.trim().toLowerCase();
                        boolean match = (p.getName() != null && p.getName().toLowerCase().contains(q))
                                || (p.getCode() != null && p.getCode().toLowerCase().contains(q))
                                || (p.getLegacyCode() != null && p.getLegacyCode().toLowerCase().contains(q))
                                || (p.getCategory() != null && p.getCategory().toLowerCase().contains(q))
                                || (p.getSize() != null && p.getSize().toLowerCase().contains(q))
                                || (p.getBrand() != null && p.getBrand().toLowerCase().contains(q))
                                || (p.getCondition() != null && p.getCondition().toLowerCase().contains(q));
                        if (!match) return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(RekidsSandboxProductDto::getId))
                .collect(Collectors.toList());

        int totalElements = filtered.size();
        int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / safeLimit);

        List<RekidsSandboxProductDto> workingList = filtered;
        if (afterUuid != null && !afterUuid.isBlank()) {
            workingList = filtered.stream()
                    .filter(p -> p.getId().compareToIgnoreCase(afterUuid) > 0)
                    .collect(Collectors.toList());
        } else if (safePage > 0) {
            int fromIndex = Math.min(safePage * safeLimit, filtered.size());
            workingList = filtered.subList(fromIndex, filtered.size());
        }

        List<RekidsSandboxProductDto> pageItems = workingList.stream()
                .limit(safeLimit)
                .collect(Collectors.toList());

        String nextCursor = null;
        if (workingList.size() > safeLimit && !pageItems.isEmpty()) {
            String lastId = pageItems.get(pageItems.size() - 1).getId();
            nextCursor = encodeCursor(branchId, lastId);
        }

        String region = resolveRegionFromBranch(branchId, null);
        String branchName = "LA_PAZ".equalsIgnoreCase(region)
                ? "DEMO La Paz — datos ficticios"
                : "DEMO Santa Cruz — datos ficticios";

        List<ProductWithImageDto> garments = pageItems.stream()
                .map(sp -> toProductWithImageDto(sp, branchName, region))
                .collect(Collectors.toList());

        return RekidsSandboxProductPageResponseDto.builder()
                .items(pageItems)
                .nextCursor(nextCursor)
                .branchId(branchId)
                .branchName(branchName)
                .region(region)
                .page(safePage)
                .limit(safeLimit)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .availableCount(totalAvailableInBranch)
                .unavailableCount(totalUnavailableInBranch)
                .garments(garments)
                .build();
    }

    private RekidsSandboxProductPageResponseDto enrichPageResponseMetadata(
            RekidsSandboxProductPageResponseDto pageDto,
            String branchId,
            int limit,
            int page
    ) {
        if (pageDto == null) return null;
        List<RekidsSandboxProductDto> items = pageDto.getItems() != null ? pageDto.getItems() : List.of();
        String region = resolveRegionFromBranch(branchId, null);
        String branchName = "LA_PAZ".equalsIgnoreCase(region)
                ? "DEMO La Paz — datos ficticios"
                : "DEMO Santa Cruz — datos ficticios";
        int available = (int) items.stream().filter(p -> "available".equalsIgnoreCase(p.getAvailability())).count();
        int unavailable = items.size() - available;
        List<ProductWithImageDto> garments = items.stream()
                .map(sp -> toProductWithImageDto(sp, branchName, region))
                .collect(Collectors.toList());

        pageDto.setBranchId(branchId);
        pageDto.setBranchName(branchName);
        pageDto.setRegion(region);
        pageDto.setPage(page);
        pageDto.setLimit(limit);
        if (pageDto.getTotalElements() == null) {
            pageDto.setTotalElements(items.size() + (pageDto.getNextCursor() != null ? 1 : 0));
        }
        if (pageDto.getTotalPages() == null) {
            pageDto.setTotalPages(pageDto.getNextCursor() != null ? page + 2 : page + 1);
        }
        pageDto.setAvailableCount(available);
        pageDto.setUnavailableCount(unavailable);
        pageDto.setGarments(garments);
        return pageDto;
    }

    /**
     * Recorre todas las páginas de una sucursal usando next_cursor (según requerimiento de LEEME.md).
     */
    @Override
    public List<RekidsSandboxProductDto> fetchAllProductsForBranch(String branchId) {
        List<RekidsSandboxProductDto> all = new ArrayList<>();
        String cursor = null;
        Set<String> seenCursors = new HashSet<>();
        while (true) {
            RekidsSandboxProductPageResponseDto page = listProductsByBranch(branchId, cursor, 50);
            all.addAll(page.getItems());
            cursor = page.getNextCursor();
            if (cursor == null || cursor.isBlank()) break;
            if (!seenCursors.add(cursor)) {
                throw new IllegalStateException("El gateway ReKids repitió un cursor de paginación.");
            }
        }
        return all;
    }

    /**
     * Consulta la ficha individual de un producto: GET /integrations/v1/products/{product_id}
     */
    @Override
    public RekidsSandboxProductDto getProductById(String productId) {
        ResponseEntity<RekidsSandboxProductDto> resp = restTemplate.exchange(
                normalizeBaseUrl(externalPortalUrl) + "/integrations/v1/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                RekidsSandboxProductDto.class);
        RekidsSandboxProductDto product = resp.getBody();
        if (product == null) {
            throw new IllegalStateException("El gateway ReKids devolvió una ficha de producto vacía.");
        }
        return product;
    }

    /**
     * Obtiene la primera página del catálogo separado por sucursal autorizada (La Paz y Santa Cruz)
     * con metadatos de paginación (next_cursor, limit, page, totalPages).
     */
    @Override
    public List<RekidsBranchCatalogDto> fetchCatalogSeparatedByBranches(String regionFilter) {
        RekidsSandboxBranchListResponseDto branchList = listBranches();
        List<RekidsSandboxBranchDto> branches = branchList.getItems();

        List<RekidsBranchCatalogDto> result = new ArrayList<>();
        for (RekidsSandboxBranchDto branch : branches) {
            String region = resolveRegionFromBranch(branch.getId(), branch.getName());
            if (regionFilter != null && !regionFilter.isBlank() && !regionFilter.equalsIgnoreCase("TODOS")) {
                if (!region.equalsIgnoreCase(regionFilter.trim().replace(" ", "_"))) {
                    continue;
                }
            }
            RekidsSandboxProductPageResponseDto pageDto = listProductsByBranchFiltered(
                    branch.getId(), null, 50, 0, null, null, null, null, null, null
            );
            List<RekidsSandboxProductDto> branchProducts = pageDto.getItems();
            int available = pageDto.getAvailableCount() != null
                    ? pageDto.getAvailableCount()
                    : (int) branchProducts.stream().filter(p -> "available".equalsIgnoreCase(p.getAvailability())).count();
            int unavailable = pageDto.getUnavailableCount() != null
                    ? pageDto.getUnavailableCount()
                    : (branchProducts.size() - available);
            int totalElements = pageDto.getTotalElements() != null
                    ? pageDto.getTotalElements()
                    : branchProducts.size();

            List<ProductWithImageDto> mappedGarments = branchProducts.stream()
                    .map(sp -> toProductWithImageDto(sp, branch.getName(), region))
                    .collect(Collectors.toList());

            result.add(RekidsBranchCatalogDto.builder()
                    .branchId(branch.getId())
                    .branchName(branch.getName())
                    .region(region)
                    .totalProducts(totalElements)
                    .availableCount(available)
                    .unavailableCount(unavailable)
                    .nextCursor(pageDto.getNextCursor())
                    .page(0)
                    .limit(50)
                    .totalPages(pageDto.getTotalPages() != null ? pageDto.getTotalPages() : 1)
                    .sandboxProducts(branchProducts)
                    .garments(mappedGarments)
                    .build());
        }
        return result;
    }

    /**
     * Reconciliación completa de caché (según LEEME.md: recorre todas las páginas con next_cursor
     * exclusivamente durante la sincronización administrativa hacia la BD local).
     */
    @Override
    public List<ExternalGarmentDto> fetchExternalGarments(String region) {
        RekidsSandboxBranchListResponseDto branchList = listBranches();
        List<RekidsSandboxBranchDto> branches = branchList.getItems();
        List<ExternalGarmentDto> all = new ArrayList<>();

        for (RekidsSandboxBranchDto branch : branches) {
            String branchRegion = resolveRegionFromBranch(branch.getId(), branch.getName());
            if (region != null && !region.isBlank() && !region.equalsIgnoreCase("TODOS")) {
                if (!branchRegion.equalsIgnoreCase(region.trim().replace(" ", "_"))) {
                    continue;
                }
            }
            List<RekidsSandboxProductDto> branchProducts = fetchAllProductsForBranch(branch.getId());
            for (RekidsSandboxProductDto sp : branchProducts) {
                all.add(toExternalGarmentDto(sp, branch.getName(), branchRegion));
            }
        }
        return all;
    }

    @Override
    public Map<String, Object> syncSaleWithExternalPortal(Map<String, Object> salePayload) {
        log.info("Sandbox ReKids v0.3.0 es de solo lectura (catálogo y stock). Registrando venta localmente.");
        Map<String, Object> fallbackResp = new HashMap<>();
        fallbackResp.put("synced", false);
        fallbackResp.put("readOnlySandbox", true);
        fallbackResp.put("status", "SANDBOX_READ_ONLY_LOCAL_SALE_RECORDED");
        return fallbackResp;
    }

    private HttpHeaders buildAuthHeaders() {
        if (configuredToken == null || !configuredToken.trim().matches("rk_test_[0-9a-f]{64}")) {
            throw new IllegalStateException("Configura una credencial de pruebas válida en REKIDS_API_TOKEN.");
        }
        HttpHeaders h = new HttpHeaders();
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        h.setBearerAuth(configuredToken.trim());
        return h;
    }

    private static String normalizeBaseUrl(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public String resolveRegionFromBranch(String branchId, String branchName) {
        if (BRANCH_LA_PAZ_ID.equalsIgnoreCase(branchId)) return "LA_PAZ";
        if (BRANCH_SANTA_CRUZ_ID.equalsIgnoreCase(branchId)) return "SANTA_CRUZ";
        if (branchName != null) {
            String upper = branchName.toUpperCase();
            if (upper.contains("PAZ")) return "LA_PAZ";
            if (upper.contains("SANTA") || upper.contains("CRUZ")) return "SANTA_CRUZ";
        }
        return "SANTA_CRUZ";
    }

    private String encodeCursor(String branchId, String afterUuid) {
        try {
            Map<String, String> cursorMap = new LinkedHashMap<>();
            cursorMap.put("branch", branchId);
            cursorMap.put("after", afterUuid);
            String json = objectMapper.writeValueAsString(cursorMap);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    private String decodeAfterFromCursor(String cursor, String branchId) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            JsonNode node = objectMapper.readTree(decoded);
            if (branchId != null && !branchId.equalsIgnoreCase(node.path("branch").asText())) {
                throw new IllegalArgumentException("Cursor no corresponde a la sucursal solicitada.");
            }
            return node.path("after").asText(null);
        } catch (Exception e) {
            return null;
        }
    }

    private ExternalGarmentDto toExternalGarmentDto(RekidsSandboxProductDto sp, String branchName, String region) {
        BigDecimal price = parsePrice(sp.getPrice());
        boolean available = "available".equalsIgnoreCase(sp.getAvailability());
        String primaryPhoto = resolvePrimaryPhotoUrl(sp);
        return ExternalGarmentDto.builder()
                .externalId(sp.getId())
                .sku(sp.getCode())
                .legacyCode(sp.getLegacyCode())
                .branchId(sp.getBranchId())
                .branchName(branchName)
                .name(sp.getName())
                .category(sp.getCategory())
                .size(sp.getSize())
                .color(sp.getCondition() != null ? "Estado: " + sp.getCondition() : "Estándar")
                .gender("Unisex")
                .brand(sp.getBrand() != null ? sp.getBrand() : "ReKids")
                .condition(sp.getCondition())
                .purchasePrice(price)
                .salePrice(price)
                .currency(sp.getCurrency() != null ? sp.getCurrency() : "BOB")
                .availability(sp.getAvailability())
                .region(region)
                .stock(available ? BigInteger.ONE : BigInteger.ZERO)
                .photo(primaryPhoto)
                .photos(sp.getPhotos() != null ? sp.getPhotos() : List.of())
                .receptionDate(System.currentTimeMillis())
                .updatedAt(sp.getUpdatedAt())
                .version(sp.getVersion())
                .build();
    }

    private ProductWithImageDto toProductWithImageDto(RekidsSandboxProductDto sp, String branchName, String region) {
        BigDecimal price = parsePrice(sp.getPrice());
        boolean available = "available".equalsIgnoreCase(sp.getAvailability());
        String primaryPhoto = resolvePrimaryPhotoUrl(sp);
        return ProductWithImageDto.builder()
                .id(sp.getId())
                .externalId(sp.getId())
                .sku(sp.getCode())
                .legacyCode(sp.getLegacyCode())
                .branchId(sp.getBranchId())
                .branchName(branchName)
                .name(sp.getName())
                .productName(sp.getName())
                .category(sp.getCategory())
                .size(sp.getSize())
                .color(sp.getCondition() != null ? "Estado: " + sp.getCondition() : "Estándar")
                .gender("Unisex")
                .brand(sp.getBrand() != null ? sp.getBrand() : "ReKids")
                .condition(sp.getCondition())
                .currency(sp.getCurrency() != null ? sp.getCurrency() : "BOB")
                .availability(sp.getAvailability())
                .purchasePrice(price)
                .salePrice(price)
                .finalPrice(price)
                .unitaryCost(price)
                .discountType("NONE")
                .discountValue(BigDecimal.ZERO)
                .region(region)
                .stock(available ? BigInteger.ONE : BigInteger.ZERO)
                .photo(primaryPhoto)
                .photos(sp.getPhotos() != null ? sp.getPhotos() : List.of())
                .isNew(true)
                .updatedAt(sp.getUpdatedAt())
                .version(sp.getVersion())
                .eventNames(List.of("Novedades", "Sandbox ReKids"))
                .build();
    }

    private BigDecimal parsePrice(String priceStr) {
        try {
            return priceStr != null ? new BigDecimal(priceStr) : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String resolvePrimaryPhotoUrl(RekidsSandboxProductDto sp) {
        if (sp.getPhotos() != null && !sp.getPhotos().isEmpty()) {
            return sp.getPhotos().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getPrimary()) && p.getUrl() != null)
                    .map(RekidsSandboxPhotoDto::getUrl)
                    .findFirst()
                    .orElse(sp.getPhotos().get(0).getUrl());
        }
        String code = sp.getCode() != null ? sp.getCode().toUpperCase() : "";
        if (code.contains("LP-001")) {
            return "https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?auto=format&fit=crop&w=600&q=80";
        }
        if (code.contains("LP-002")) {
            return "https://images.unsplash.com/photo-1503944583220-79d8926ad5e2?auto=format&fit=crop&w=600&q=80";
        }
        if (code.contains("SC-001")) {
            return "https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=600&q=80";
        }
        if (code.contains("SC-002")) {
            return "https://images.unsplash.com/photo-1544923246-77307dd654cb?auto=format&fit=crop&w=600&q=80";
        }
        return "https://images.unsplash.com/photo-1522771930-78848d9293e8?auto=format&fit=crop&w=600&q=80";
    }
}

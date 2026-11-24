package com.upb.cores.impl;

import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.ProductService;
import com.upb.cores.StoreEventService;
import com.upb.cores.rekids.client.RekidsExternalPortalFeignClient;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.event.EventProduct;
import com.upb.models.event.StoreEvent;
import com.upb.models.product.Product;
import com.upb.models.product.dto.*;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.Warehouse;
import com.upb.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final WarehouseRepository warehouseRepository;
    private final EventProductRepository eventProductRepository;
    private final StoreEventService storeEventService;
    private final RekidsExternalPortalFeignClient externalPortalFeignClient;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListDto> getProductsList(Authentication auth, String productName, String category, Pageable pageable) {
        productName = (!StringUtil.isNullOrEmpty(productName) ? "%" + productName.toUpperCase() + "%" : null);
        category = (!StringUtil.isNullOrEmpty(category) ? "%" + category.toUpperCase() + "%" : null);

        Enterprise enterprise = resolveEnterprise(auth);
        if (enterprise != null) {
            return productRepository.getProductPageable(enterprise.getId(), productName, category, pageable);
        }
        return productRepository.findAll(pageable).map(ProductListDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductWithImageDto> getProductsWithImagesList(Authentication auth, String productName, String category, Pageable pageable) {
        String nameParam = (!StringUtil.isNullOrEmpty(productName) ? "%" + productName.toUpperCase() + "%" : null);
        String catParam = (!StringUtil.isNullOrEmpty(category) ? "%" + category.toUpperCase() + "%" : null);

        List<User_BranchOffice> ub = resolveUserBranches(auth);
        if (!ub.isEmpty()) {
            return productRepository.getProductWithImagePageable(
                    ub.get(0).getBranchOffice().getEnterprise().getId(),
                    ub.get(0).getBranchOffice().getId(),
                    nameParam,
                    catParam,
                    pageable
            );
        }
        return productRepository.getRekidsProductsByRegion(
                "",
                catParam != null ? catParam : "",
                nameParam != null ? nameParam : "",
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductListDto> getProductsListByCategory(Authentication auth, String category) {
        Enterprise enterprise = resolveEnterprise(auth);
        if (enterprise != null) {
            return productRepository.getEnterpriseProductsListByCategoryAndStateTrue(enterprise.getId(), category.toUpperCase());
        }
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(String idProduct) {
        return productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );
    }

    @Override
    public ProductDto createProduct(Authentication auth, String name, String category, String beverageFormat, String sku, String photo) {
        StringUtilMod.notNullStringMaxLength(name, 60, "nombre");
        StringUtilMod.notNullStringMaxLength(category, 30, "categoría");
        StringUtilMod.notNullStringMaxLength(sku, 50, "sku/código producto");

        if (category.equalsIgnoreCase("BEBIDA")) {
            StringUtilMod.notNullStringMaxLength(beverageFormat, 30, "formato bebida");
            beverageFormat = StringUtilMod.capitalizeFirstLetter(beverageFormat);
        }

        Enterprise enterprise = resolveEnterprise(auth);

        Product product = Product.builder()
                .name(name)
                .category(StringUtilMod.capitalizeFirstLetter(category))
                .beverageFormat(beverageFormat)
                .state(true)
                .sku(sku.toUpperCase())
                .enterprise(enterprise)
                .photo(photo)
                .isNew(true)
                .build();

        productRepository.save(product);

        return new ProductDto(product);
    }

    @Override
    public ProductDto updateProduct(String idProduct, String name, String category, String beverageFormat, String sku, String photo) {
        StringUtilMod.notNullStringMaxLength(name, 60, "nombre");
        StringUtilMod.notNullStringMaxLength(category, 30, "categoría");
        StringUtilMod.notNullStringMaxLength(sku, 50, "sku/código producto");

        if (category.equalsIgnoreCase("BEBIDA")) {
            StringUtilMod.notNullStringMaxLength(beverageFormat, 30, "formato bebida");
        } else {
            beverageFormat = "";
        }

        Product product = productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );

        product.setName(name);
        product.setCategory(category);
        product.setBeverageFormat(beverageFormat);
        product.setSku(sku.toUpperCase());
        product.setPhoto(photo);
        productRepository.save(product);

        return new ProductDto(product);
    }

    @Override
    public ProductDto deleteProduct(String idProduct) {
        Product product = productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );

        product.setState(false);
        productRepository.save(product);

        return new ProductDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductWithImageDto> getProductsByRegion(String region, String eventId, String category, String filter, Pageable pageable) {
        return getProductsByRegionFiltered(region, null, eventId, category, null, null, null, null, filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductWithImageDto> getProductsByRegionFiltered(
            String region,
            String branchId,
            String eventId,
            String category,
            String clothingSize,
            String brand,
            String condition,
            String availability,
            String filter,
            Pageable pageable
    ) {
        if (StringUtil.isNullOrEmpty(region)) {
            throw new IllegalArgumentException("Debe seleccionar primero la región (SANTA_CRUZ, LA_PAZ o TODOS).");
        }
        String normRegion = (region.equalsIgnoreCase("TODOS") || region.equalsIgnoreCase("ALL"))
                ? ""
                : normalizeRegion(region);
        if (normRegion == null) normRegion = "";
        String branchParam = (!StringUtil.isNullOrEmpty(branchId) && !branchId.equalsIgnoreCase("TODOS") && !branchId.equalsIgnoreCase("Todas"))
                ? branchId.trim().toUpperCase() : "";
        String catParam = (!StringUtil.isNullOrEmpty(category) && !category.equalsIgnoreCase("Todas") && !category.equalsIgnoreCase("TODOS"))
                ? "%" + category.trim().toUpperCase() + "%" : "";
        String sizeParam = (!StringUtil.isNullOrEmpty(clothingSize) && !clothingSize.equalsIgnoreCase("Todas") && !clothingSize.equalsIgnoreCase("TODOS"))
                ? "%" + clothingSize.trim().toUpperCase() + "%" : "";
        String brandParam = (!StringUtil.isNullOrEmpty(brand) && !brand.equalsIgnoreCase("Todas") && !brand.equalsIgnoreCase("TODOS"))
                ? "%" + brand.trim().toUpperCase() + "%" : "";
        String conditionParam = (!StringUtil.isNullOrEmpty(condition) && !condition.equalsIgnoreCase("Todas") && !condition.equalsIgnoreCase("TODOS"))
                ? "%" + condition.trim().toUpperCase() + "%" : "";
        String availabilityParam = (!StringUtil.isNullOrEmpty(availability) && !availability.equalsIgnoreCase("Todas") && !availability.equalsIgnoreCase("TODOS"))
                ? availability.trim().toUpperCase() : "";
        String filterParam = (!StringUtil.isNullOrEmpty(filter))
                ? "%" + filter.trim().toUpperCase() + "%" : "";

        Page<ProductWithImageDto> page;
        if (!StringUtil.isNullOrEmpty(eventId) && !eventId.equalsIgnoreCase("TODOS") && !eventId.equalsIgnoreCase("Todas")) {
            page = productRepository.getRekidsProductsFilteredByEventPageable(
                    normRegion,
                    branchParam,
                    eventId.trim(),
                    eventId.trim().toUpperCase(),
                    catParam,
                    sizeParam,
                    brandParam,
                    conditionParam,
                    availabilityParam,
                    filterParam,
                    pageable
            );
        } else {
            page = productRepository.getRekidsProductsFilteredPageable(
                    normRegion,
                    branchParam,
                    catParam,
                    sizeParam,
                    brandParam,
                    conditionParam,
                    availabilityParam,
                    filterParam,
                    pageable
            );
        }

        enrichBatch(page.getContent());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithImageDto getProductByCodeAndRegion(String code, String region) {
        if (StringUtil.isNullOrEmpty(region)) {
            throw new IllegalArgumentException("Debe seleccionar primero la región (SANTA_CRUZ o LA_PAZ).");
        }
        if (StringUtil.isNullOrEmpty(code)) {
            throw new IllegalArgumentException("Debe ingresar el código (SKU) de la prenda.");
        }
        String normRegion = normalizeRegion(region);

        Product product = productRepository.findBySkuAndRegion(code.trim(), normRegion)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se encontró ninguna prenda con el código [" + code.toUpperCase() + "] en la tienda de " + normRegion.replace("_", " ")
                ));

        ProductWithImageDto dto = new ProductWithImageDto(product);
        enrichWithEventsAndStock(dto);
        return dto;
    }

    @Override
    @Transactional
    public SyncGarmentsResultDto syncGarmentsFromExternalPortal(Authentication auth, String region, List<ExternalGarmentDto> externalGarmentsPayload) {
        // Desactivar cualquier prenda mockeada anterior (RK-SCZ-*, RK-LPZ-*, EXT-*) para que solo queden datos reales del API del tercero
        productRepository.deactivateMockedGarments();

        String normRegion = (!StringUtil.isNullOrEmpty(region) && !region.equalsIgnoreCase("TODOS"))
                ? normalizeRegion(region) : null;

        List<ExternalGarmentDto> sourceGarments = (externalGarmentsPayload != null && !externalGarmentsPayload.isEmpty())
                ? externalGarmentsPayload
                : externalPortalFeignClient.fetchExternalGarments(normRegion);

        Enterprise enterprise = resolveEnterprise(auth);
        StoreEvent novedadesEvent = storeEventService.getOrCreateNovedadesEvent();
        storeEventService.getOrCreateDescuentosEvent();

        int createdCount = 0;
        int updatedCount = 0;
        int addedToNovedadesCount = 0;
        long now = System.currentTimeMillis();
        List<ProductWithImageDto> syncedDtos = new ArrayList<>();

        for (ExternalGarmentDto ext : sourceGarments) {
            String garmentRegion = normalizeRegion(ext.getRegion() != null ? ext.getRegion() : (normRegion != null ? normRegion : "SANTA_CRUZ"));
            Optional<Product> existingOpt = Optional.empty();
            if (!StringUtil.isNullOrEmpty(ext.getExternalId())) {
                existingOpt = productRepository.findByExternalId(ext.getExternalId());
            }
            if (existingOpt.isEmpty() && !StringUtil.isNullOrEmpty(ext.getSku())) {
                existingOpt = productRepository.findBySkuAndRegion(ext.getSku(), garmentRegion);
            }

            String resolvedAvailability = ext.getAvailability() != null
                    ? ext.getAvailability().toLowerCase()
                    : ((ext.getStock() != null && ext.getStock().compareTo(BigInteger.ZERO) > 0) ? "available" : "unavailable");

            Product product;
            boolean isNewRecord = existingOpt.isEmpty();
            if (isNewRecord) {
                product = Product.builder()
                        .externalId(ext.getExternalId())
                        .sku(ext.getSku() != null ? ext.getSku().toUpperCase() : "RK-" + System.currentTimeMillis())
                        .legacyCode(ext.getLegacyCode())
                        .branchId(ext.getBranchId())
                        .name(ext.getName())
                        .category(ext.getCategory() != null ? ext.getCategory() : "Prendas")
                        .size(ext.getSize())
                        .color(ext.getColor())
                        .gender(ext.getGender())
                        .brand(ext.getBrand())
                        .condition(ext.getCondition() != null ? ext.getCondition() : "Excelente")
                        .availability(resolvedAvailability)
                        .updatedAt(ext.getUpdatedAt())
                        .version(ext.getVersion() != null ? ext.getVersion() : 1L)
                        .purchasePrice(ext.getPurchasePrice())
                        .salePrice(ext.getSalePrice())
                        .finalPrice(ext.getSalePrice())
                        .discountType("NONE")
                        .discountValue(BigDecimal.ZERO)
                        .receptionDate(ext.getReceptionDate() != null ? ext.getReceptionDate() : now)
                        .region(garmentRegion)
                        .photo(ext.getPhoto())
                        .enterprise(enterprise)
                        .state(true)
                        .isNew(true)
                        .lastSyncDate(now)
                        .build();
                createdCount++;
            } else {
                product = existingOpt.get();
                product.setName(ext.getName() != null ? ext.getName() : product.getName());
                product.setCategory(ext.getCategory() != null ? ext.getCategory() : product.getCategory());
                product.setSize(ext.getSize() != null ? ext.getSize() : product.getSize());
                product.setColor(ext.getColor() != null ? ext.getColor() : product.getColor());
                product.setGender(ext.getGender() != null ? ext.getGender() : product.getGender());
                product.setBrand(ext.getBrand() != null ? ext.getBrand() : product.getBrand());
                if (ext.getLegacyCode() != null) product.setLegacyCode(ext.getLegacyCode());
                if (ext.getBranchId() != null) product.setBranchId(ext.getBranchId());
                if (ext.getCondition() != null) product.setCondition(ext.getCondition());
                product.setAvailability(resolvedAvailability);
                if (ext.getUpdatedAt() != null) product.setUpdatedAt(ext.getUpdatedAt());
                if (ext.getVersion() != null) product.setVersion(ext.getVersion());
                if (ext.getPurchasePrice() != null) product.setPurchasePrice(ext.getPurchasePrice());
                if (ext.getSalePrice() != null) {
                    product.setSalePrice(ext.getSalePrice());
                    if (product.getDiscountValue() == null || product.getDiscountValue().compareTo(BigDecimal.ZERO) == 0) {
                        product.setFinalPrice(ext.getSalePrice());
                    }
                }
                if (ext.getPhoto() != null) product.setPhoto(ext.getPhoto());
                product.setRegion(garmentRegion);
                product.setState(true);
                product.setLastSyncDate(now);
                if (product.getIsNew() == null) {
                    product.setIsNew(true);
                }
                updatedCount++;
            }

            product = productRepository.save(product);

            // Vincular a evento permanente NOVEDADES si es nueva
            if (Boolean.TRUE.equals(product.getIsNew())) {
                final Product savedProd = product;
                Optional<EventProduct> epOpt = eventProductRepository.findByEventIdAndProductId(novedadesEvent.getId(), savedProd.getId());
                if (epOpt.isEmpty()) {
                    EventProduct ep = EventProduct.builder()
                            .event(novedadesEvent)
                            .product(savedProd)
                            .originalPrice(savedProd.getSalePrice())
                            .finalPrice(savedProd.getFinalPrice() != null ? savedProd.getFinalPrice() : savedProd.getSalePrice())
                            .discountType(savedProd.getDiscountType() != null ? savedProd.getDiscountType() : "NONE")
                            .discountValue(savedProd.getDiscountValue() != null ? savedProd.getDiscountValue() : BigDecimal.ZERO)
                            .approvedByAdminName(resolveAdminName(auth))
                            .approvedDate(now)
                            .state(true)
                            .build();
                    eventProductRepository.save(ep);
                    addedToNovedadesCount++;
                }
            }

            // Sincronizar también en Warehouse de la sucursal de la región si existe
            syncWarehouseStockForGarment(product, garmentRegion, ext.getStock());

            ProductWithImageDto dto = new ProductWithImageDto(product);
            enrichWithEventsAndStock(dto);
            syncedDtos.add(dto);
        }

        return SyncGarmentsResultDto.builder()
                .totalFetched(sourceGarments.size())
                .createdCount(createdCount)
                .updatedCount(updatedCount)
                .addedToNovedadesCount(addedToNovedadesCount)
                .syncedByAdmin(resolveAdminName(auth))
                .syncTimestamp(now)
                .source("RekidsExternalPortalFeignClient")
                .garments(syncedDtos)
                .build();
    }

    private void syncWarehouseStockForGarment(Product product, String region, BigInteger stock) {
        try {
            List<BranchOffice> branches = branchOfficeRepository.findAll();
            Optional<BranchOffice> regionBranch = branches.stream()
                    .filter(b -> !"DELETED".equalsIgnoreCase(b.getState()))
                    .filter(b -> (b.getLocation() != null && b.getLocation().toUpperCase().contains(region.replace("_", " ")))
                            || (b.getName() != null && b.getName().toUpperCase().contains(region.replace("_", " "))))
                    .findFirst();

            if (regionBranch.isPresent()) {
                BranchOffice branch = regionBranch.get();
                Warehouse warehouse = warehouseRepository.findByBranchOfficeIdAndProductId(branch.getId(), product.getId())
                        .orElseGet(() -> Warehouse.builder()
                                .branchOffice(branch)
                                .product(product)
                                .state("ACTIVE")
                                .maxProduct(BigInteger.TEN)
                                .minProduct(BigInteger.ONE)
                                .build());
                warehouse.setStock(stock != null ? stock : BigInteger.ONE);
                warehouse.setUnitaryCost(product.getFinalPrice() != null ? product.getFinalPrice() : product.getSalePrice());
                warehouseRepository.save(warehouse);
            }
        } catch (Exception e) {
            log.warn("No se pudo actualizar registro de almacén para prenda {}: {}", product.getSku(), e.getMessage());
        }
    }

    /** Enriquece una lista completa con 2 consultas en total (en vez de 2 por prenda). */
    private void enrichBatch(List<ProductWithImageDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return;
        List<String> ids = dtos.stream().map(ProductWithImageDto::getId)
                .filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) return;
        try {
            java.util.Map<String, List<String>> eventsByProduct = new java.util.HashMap<>();
            for (EventProduct ep : eventProductRepository.findActiveByProductIds(ids)) {
                eventsByProduct.computeIfAbsent(ep.getProduct().getId(), k -> new ArrayList<>())
                        .add(ep.getEvent().getName());
            }
            java.util.Map<String, Warehouse> stockByProduct = new java.util.HashMap<>();
            for (Warehouse w : warehouseRepository.getWarehousesListByIdProductListReadOnly(ids)) {
                stockByProduct.putIfAbsent(w.getProduct().getId(), w);
            }
            for (ProductWithImageDto dto : dtos) {
                if (dto.getId() == null) continue;
                List<String> names = eventsByProduct.getOrDefault(dto.getId(), new ArrayList<>())
                        .stream().distinct().collect(Collectors.toList());
                if (Boolean.TRUE.equals(dto.getIsNew()) && names.stream().noneMatch(n -> n.equalsIgnoreCase("Novedades"))) {
                    names.add(0, "Novedades");
                }
                dto.setEventNames(names);
                Warehouse w = stockByProduct.get(dto.getId());
                if (w != null) {
                    BigInteger wStock = w.getStock();
                    dto.setStock(wStock);
                    dto.setAvailability((wStock != null && wStock.compareTo(BigInteger.ZERO) > 0) ? "available" : "unavailable");
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo enriquecer eventos/stock en lote: {}", e.getMessage());
        }
    }

    private void enrichWithEventsAndStock(ProductWithImageDto dto) {
        if (dto == null || dto.getId() == null) return;
        try {
            List<EventProduct> activeEvents = eventProductRepository.findActiveByProductId(dto.getId());
            List<String> names = activeEvents.stream()
                    .map(ep -> ep.getEvent().getName())
                    .distinct()
                    .collect(Collectors.toList());
            if (Boolean.TRUE.equals(dto.getIsNew()) && names.stream().noneMatch(n -> n.equalsIgnoreCase("Novedades"))) {
                names.add(0, "Novedades");
            }
            dto.setEventNames(names);

            List<Warehouse> warehouses = warehouseRepository.getWarehousesListByIdProductListReadOnly(List.of(dto.getId()));
            if (!warehouses.isEmpty()) {
                BigInteger wStock = warehouses.get(0).getStock();
                dto.setStock(wStock);
                dto.setAvailability((wStock != null && wStock.compareTo(BigInteger.ZERO) > 0) ? "available" : "unavailable");
            }
        } catch (Exception e) {
            log.debug("No se pudo enriquecer eventos/stock para {}: {}", dto.getId(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RekidsSandboxBranchDto> getRekidsBranches() {
        RekidsSandboxBranchListResponseDto response = externalPortalFeignClient.listBranches();
        return response != null && response.getItems() != null ? response.getItems() : List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public RekidsSandboxProductPageResponseDto getRekidsProductsByBranch(String branchId, String cursor, Integer limit) {
        if (StringUtil.isNullOrEmpty(branchId)) {
            throw new IllegalArgumentException("El parámetro branch_id es obligatorio.");
        }
        return externalPortalFeignClient.listProductsByBranch(branchId.trim(), cursor, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public RekidsSandboxProductPageResponseDto getRekidsProductsByBranchFiltered(
            String branchId,
            String cursor,
            Integer limit,
            Integer page,
            String filter,
            String category,
            String clothingSize,
            String brand,
            String condition,
            String availability
    ) {
        if (StringUtil.isNullOrEmpty(branchId)) {
            throw new IllegalArgumentException("El parámetro branch_id es obligatorio.");
        }
        return externalPortalFeignClient.listProductsByBranchFiltered(
                branchId.trim(),
                cursor,
                limit,
                page,
                filter,
                category,
                clothingSize,
                brand,
                condition,
                availability
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RekidsSandboxProductDto getRekidsSandboxProductById(String productId) {
        if (StringUtil.isNullOrEmpty(productId)) {
            throw new IllegalArgumentException("El parámetro product_id es obligatorio.");
        }
        return externalPortalFeignClient.getProductById(productId.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RekidsBranchCatalogDto> getRekidsCatalogSeparatedByBranches(String region) {
        String normRegion = (!StringUtil.isNullOrEmpty(region) && !region.equalsIgnoreCase("TODOS"))
                ? normalizeRegion(region) : null;
        List<RekidsBranchCatalogDto> catalogs = externalPortalFeignClient.fetchCatalogSeparatedByBranches(normRegion);
        List<BranchOffice> localBranches = branchOfficeRepository.findAll();

        for (RekidsBranchCatalogDto cat : catalogs) {
            String regKey = cat.getRegion() != null ? cat.getRegion().replace("_", " ") : "";
            localBranches.stream()
                    .filter(b -> !"DELETED".equalsIgnoreCase(b.getState()))
                    .filter(b -> (b.getLocation() != null && b.getLocation().toUpperCase().replace("_", " ").contains(regKey))
                            || (b.getName() != null && b.getName().toUpperCase().contains(regKey)))
                    .findFirst()
                    .ifPresent(b -> cat.setLocalBranchOfficeId(b.getId()));
        }
        return catalogs;
    }

    private String normalizeRegion(String region) {
        if (StringUtil.isNullOrEmpty(region)) return null;
        String upper = region.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        if (upper.contains("SANTA") || upper.equals("SCZ")) return "SANTA_CRUZ";
        if (upper.contains("PAZ") || upper.equals("LPZ") || upper.equals("LP")) return "LA_PAZ";
        return upper;
    }

    private List<User_BranchOffice> resolveUserBranches(Authentication auth) {
        try {
            if (auth != null && auth.getPrincipal() instanceof User user && !auth.getAuthorities().isEmpty()) {
                String idRol = auth.getAuthorities().stream().toList().get(0).toString();
                return userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(), idRol);
            }
        } catch (Exception e) {
            log.debug("No se pudo resolver User_BranchOffice desde auth: {}", e.getMessage());
        }
        return List.of();
    }

    private Enterprise resolveEnterprise(Authentication auth) {
        List<User_BranchOffice> ub = resolveUserBranches(auth);
        if (!ub.isEmpty() && ub.get(0).getBranchOffice() != null) {
            return ub.get(0).getBranchOffice().getEnterprise();
        }
        return enterpriseRepository.findAll().stream().findFirst().orElse(null);
    }

    private String resolveAdminName(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return ((user.getName() != null ? user.getName() : "") + " "
                    + (user.getLastname() != null ? user.getLastname() : "")).trim()
                    + " (" + user.getEmail() + ")";
        }
        return "ADMIN Rekids";
    }
}

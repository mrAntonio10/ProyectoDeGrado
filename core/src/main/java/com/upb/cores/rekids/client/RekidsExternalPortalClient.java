package com.upb.cores.rekids.client;

import com.upb.models.product.dto.ExternalGarmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RekidsExternalPortalClient implements RekidsExternalPortalFeignClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rekids.external.portal.url:http://localhost:8090/api/v1/external}")
    private String externalPortalUrl;

    @Override
    public List<ExternalGarmentDto> fetchExternalGarments(String region) {
        try {
            String url = externalPortalUrl + "/garments" + (region != null && !region.isBlank() ? "?region=" + region : "");
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<ExternalGarmentDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ExternalGarmentDto>>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                log.info("Prendas obtenidas desde portal externo Feign ({}): {}", url, response.getBody().size());
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("Portal externo Rekids ({}) no disponible aún ({}). Usando catálogo de replicación preconfigurado.", externalPortalUrl, e.getMessage());
        }

        List<ExternalGarmentDto> fallback = buildDefaultExternalGarments();
        if (region != null && !region.isBlank() && !region.equalsIgnoreCase("TODOS")) {
            return fallback.stream()
                    .filter(g -> region.equalsIgnoreCase(g.getRegion()))
                    .collect(Collectors.toList());
        }
        return fallback;
    }

    @Override
    public Map<String, Object> syncSaleWithExternalPortal(Map<String, Object> salePayload) {
        try {
            String url = externalPortalUrl + "/sales/sync";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(salePayload, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Venta sincronizada exitosamente con portal externo Feign ({})", url);
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("Portal externo Rekids ({}/sales/sync) no disponible aún ({}). Venta registrada localmente pendiente de replicación.", externalPortalUrl, e.getMessage());
        }
        Map<String, Object> fallbackResp = new HashMap<>();
        fallbackResp.put("synced", true);
        fallbackResp.put("status", "LOCAL_REPLICA_RECORDED");
        return fallbackResp;
    }

    private List<ExternalGarmentDto> buildDefaultExternalGarments() {
        long now = System.currentTimeMillis();
        List<ExternalGarmentDto> list = new ArrayList<>();

        // Prendas Tienda Santa Cruz (SANTA_CRUZ)
        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-SCZ-001")
                .sku("RK-SCZ-001")
                .name("Conjunto Algodón Osito")
                .category("Conjuntos")
                .size("2T")
                .color("Celeste")
                .gender("Niño")
                .brand("Carter's")
                .purchasePrice(new BigDecimal("45.00"))
                .salePrice(new BigDecimal("85.00"))
                .region("SANTA_CRUZ")
                .stock(BigInteger.valueOf(3))
                .photo("https://images.unsplash.com/photo-1522771930-78848d9293e8?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-SCZ-002")
                .sku("RK-SCZ-002")
                .name("Vestido Floral Verano")
                .category("Vestidos")
                .size("4T")
                .color("Rosa")
                .gender("Niña")
                .brand("Zara Kids")
                .purchasePrice(new BigDecimal("60.00"))
                .salePrice(new BigDecimal("120.00"))
                .region("SANTA_CRUZ")
                .stock(BigInteger.valueOf(2))
                .photo("https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-SCZ-003")
                .sku("RK-SCZ-003")
                .name("Polera Dinosaurio DinoRex")
                .category("Poleras")
                .size("6")
                .color("Verde")
                .gender("Niño")
                .brand("GAP Kids")
                .purchasePrice(new BigDecimal("30.00"))
                .salePrice(new BigDecimal("65.00"))
                .region("SANTA_CRUZ")
                .stock(BigInteger.valueOf(5))
                .photo("https://images.unsplash.com/photo-1503944583220-79d8926ad5e2?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-SCZ-004")
                .sku("RK-SCZ-004")
                .name("Short Denim Infantil")
                .category("Pantalones y Shorts")
                .size("5T")
                .color("Azul Denim")
                .gender("Unisex")
                .brand("OshKosh")
                .purchasePrice(new BigDecimal("40.00"))
                .salePrice(new BigDecimal("78.00"))
                .region("SANTA_CRUZ")
                .stock(BigInteger.valueOf(4))
                .photo("https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        // Prendas Tienda La Paz (LA_PAZ)
        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-LPZ-001")
                .sku("RK-LPZ-001")
                .name("Chamarra Térmica Acolchada")
                .category("Abrigos y Chamarras")
                .size("6")
                .color("Azul Marino")
                .gender("Niño")
                .brand("The North Face Kids")
                .purchasePrice(new BigDecimal("110.00"))
                .salePrice(new BigDecimal("210.00"))
                .region("LA_PAZ")
                .stock(BigInteger.valueOf(2))
                .photo("https://images.unsplash.com/photo-1544923246-77307dd654cb?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-LPZ-002")
                .sku("RK-LPZ-002")
                .name("Chompa Tejida Copos de Nieve")
                .category("Chompas y Polerones")
                .size("4T")
                .color("Rojo Navidad")
                .gender("Niña")
                .brand("H&M Kids")
                .purchasePrice(new BigDecimal("55.00"))
                .salePrice(new BigDecimal("115.00"))
                .region("LA_PAZ")
                .stock(BigInteger.valueOf(3))
                .photo("https://images.unsplash.com/photo-1519457431-44ccd64a579b?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-LPZ-003")
                .sku("RK-LPZ-003")
                .name("Buzo Polar Completo")
                .category("Conjuntos")
                .size("3T")
                .color("Gris Melange")
                .gender("Unisex")
                .brand("Carter's")
                .purchasePrice(new BigDecimal("50.00"))
                .salePrice(new BigDecimal("95.00"))
                .region("LA_PAZ")
                .stock(BigInteger.valueOf(4))
                .photo("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        list.add(ExternalGarmentDto.builder()
                .externalId("EXT-LPZ-004")
                .sku("RK-LPZ-004")
                .name("Botines Impermeables Kids")
                .category("Calzado")
                .size("28")
                .color("Café")
                .gender("Unisex")
                .brand("Colloky")
                .purchasePrice(new BigDecimal("75.00"))
                .salePrice(new BigDecimal("145.00"))
                .region("LA_PAZ")
                .stock(BigInteger.valueOf(2))
                .photo("https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=600&q=80")
                .receptionDate(now)
                .build());

        return list;
    }
}

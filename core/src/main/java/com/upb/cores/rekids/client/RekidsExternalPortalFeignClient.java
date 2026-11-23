package com.upb.cores.rekids.client;

import com.upb.models.product.dto.ExternalGarmentDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Contrato FeignClient para integración con el portal externo de prendas Rekids.
 * Sincroniza productos, stock y ventas con el otro sistema externo:
 * @FeignClient(name = "rekids-external-portal", url = "${rekids.external.portal.url}")
 */
public interface RekidsExternalPortalFeignClient {

    @GetMapping("/garments")
    List<ExternalGarmentDto> fetchExternalGarments(@RequestParam(value = "region", required = false) String region);

    @PostMapping("/sales/sync")
    Map<String, Object> syncSaleWithExternalPortal(@RequestBody Map<String, Object> salePayload);
}

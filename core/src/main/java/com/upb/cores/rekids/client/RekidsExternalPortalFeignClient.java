package com.upb.cores.rekids.client;

import com.upb.models.product.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrato FeignClient para integración con ReKids API Sandbox v1 (openapi.json)
 * Endpoints oficiales de solo lectura:
 * - GET /integrations/v1/branches
 * - GET /integrations/v1/products?branch_id={UUID}&cursor={cursor}&limit={limit}
 * - GET /integrations/v1/products/{product_id}
 */
public interface RekidsExternalPortalFeignClient {

    @GetMapping("/integrations/v1/branches")
    RekidsSandboxBranchListResponseDto listBranches();

    @GetMapping("/integrations/v1/products")
    RekidsSandboxProductPageResponseDto listProductsByBranch(
            @RequestParam("branch_id") String branchId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "50") Integer limit
    );

    RekidsSandboxProductPageResponseDto listProductsByBranchFiltered(
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
    );

    @GetMapping("/integrations/v1/products/{product_id}")
    RekidsSandboxProductDto getProductById(@PathVariable("product_id") String productId);

    List<RekidsSandboxProductDto> fetchAllProductsForBranch(String branchId);

    List<RekidsBranchCatalogDto> fetchCatalogSeparatedByBranches(String regionFilter);

    @GetMapping("/garments")
    List<ExternalGarmentDto> fetchExternalGarments(@RequestParam(value = "region", required = false) String region);

    @PostMapping("/sales/sync")
    Map<String, Object> syncSaleWithExternalPortal(@RequestBody Map<String, Object> salePayload);
}

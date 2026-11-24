package com.upb.cores;

import com.upb.models.product.Product;
import com.upb.models.product.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Page<ProductListDto> getProductsList(Authentication auth, String productName, String category, Pageable pageable);
    Page<ProductWithImageDto> getProductsWithImagesList(Authentication auth, String productName, String category, Pageable pageable);
    List<ProductListDto> getProductsListByCategory(Authentication auth, String category);
    Product getProductById(String idProduct);
    ProductDto createProduct(Authentication auth, String name, String category, String beverageFormat, String sku, String photo);
    ProductDto updateProduct(String idProduct, String name, String category, String beverageFormat, String sku, String photo);

    ProductDto deleteProduct(String idProduct);

    /* Métodos Rekids - Tienda Online por Región, Código y Sincronización FeignClient */
    Page<ProductWithImageDto> getProductsByRegion(String region, String eventId, String category, String filter, Pageable pageable);
    Page<ProductWithImageDto> getProductsByRegionFiltered(
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
    );
    ProductWithImageDto getProductByCodeAndRegion(String code, String region);
    SyncGarmentsResultDto syncGarmentsFromExternalPortal(Authentication auth, String region, List<ExternalGarmentDto> externalGarmentsPayload);

    /* Métodos ReKids Sandbox API v1 (FeignClient separados por sucursal) */
    List<RekidsSandboxBranchDto> getRekidsBranches();
    RekidsSandboxProductPageResponseDto getRekidsProductsByBranch(String branchId, String cursor, Integer limit);
    RekidsSandboxProductPageResponseDto getRekidsProductsByBranchFiltered(
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
    );
    RekidsSandboxProductDto getRekidsSandboxProductById(String productId);
    List<RekidsBranchCatalogDto> getRekidsCatalogSeparatedByBranches(String region);
}

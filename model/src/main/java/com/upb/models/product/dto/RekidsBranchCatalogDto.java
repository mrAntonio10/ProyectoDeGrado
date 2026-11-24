package com.upb.models.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RekidsBranchCatalogDto {
    private String branchId;
    private String branchName;
    private String localBranchOfficeId;
    private String region;
    private int totalProducts;
    private int availableCount;
    private int unavailableCount;
    private String nextCursor;
    private Integer page;
    private Integer limit;
    private Integer totalPages;
    private List<RekidsSandboxProductDto> sandboxProducts;
    private List<ProductWithImageDto> garments;
}

package com.upb.models.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RekidsSandboxProductPageResponseDto {
    private List<RekidsSandboxProductDto> items;

    @JsonProperty("next_cursor")
    private String nextCursor;

    private String branchId;
    private String branchName;
    private String region;
    private Integer page;
    private Integer limit;
    private Integer totalElements;
    private Integer totalPages;
    private Integer availableCount;
    private Integer unavailableCount;
    private List<ProductWithImageDto> garments;
}

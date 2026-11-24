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
public class RekidsSandboxProductDto {
    private String id;
    private String code;

    @JsonProperty("legacy_code")
    private String legacyCode;

    @JsonProperty("branch_id")
    private String branchId;

    private String name;
    private String category;
    private String size;
    private String brand;
    private String condition;
    private String price;
    private String currency;
    private String availability;
    private List<RekidsSandboxPhotoDto> photos;

    @JsonProperty("updated_at")
    private String updatedAt;

    private Long version;
}

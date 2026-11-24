package com.upb.models.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGarmentDto {
    private String externalId;
    private String sku;
    private String legacyCode;
    private String branchId;
    private String branchName;
    private String name;
    private String category;
    private String size;
    private String color;
    private String gender;
    private String brand;
    private String condition;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String currency;
    private String availability;
    private String region;
    private BigInteger stock;
    private String photo;
    private List<RekidsSandboxPhotoDto> photos;
    private Long receptionDate;
    private String updatedAt;
    private Long version;
}

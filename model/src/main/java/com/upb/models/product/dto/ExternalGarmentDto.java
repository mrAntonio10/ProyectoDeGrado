package com.upb.models.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGarmentDto {
    private String externalId;
    private String sku;
    private String name;
    private String category;
    private String size;
    private String color;
    private String gender;
    private String brand;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String region;
    private BigInteger stock;
    private String photo;
    private Long receptionDate;
}

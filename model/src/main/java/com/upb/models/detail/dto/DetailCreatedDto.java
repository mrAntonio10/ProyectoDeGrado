package com.upb.models.detail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class DetailCreatedDto {
    private String productName;
    private BigDecimal totalPrice;
    private BigInteger quantity;

}

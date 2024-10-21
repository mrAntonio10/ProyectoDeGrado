package com.upb.models.detail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class DetailListRequest {
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;
    private BigInteger quantity;
    private BigDecimal unitaryCost;

    private String idProduct;

}

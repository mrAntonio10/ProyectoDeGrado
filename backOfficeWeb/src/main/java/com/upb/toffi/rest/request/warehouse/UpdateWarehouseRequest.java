package com.upb.toffi.rest.request.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class UpdateWarehouseRequest {
    private String id;
    private String idProduct;
    private String idBranchOffice;
    private BigInteger stock;
    private BigDecimal unitaryCost;
    private BigInteger maxProduct;
    private BigInteger minProduct;
    private String state;
    private String sku;
}

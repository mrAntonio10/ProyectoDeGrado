package com.upb.models.detail.dto;

import com.upb.models.detail.OrderDetail;
import com.upb.models.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class DetailInfoDto {
    private String sku;
    private String productName;
    private BigInteger quantity;
    private BigDecimal unitaryPrice;
    private BigDecimal productDiscount;
    private BigDecimal total;

    public DetailInfoDto(AllDetailInfoDto d) {
        this.sku = d.getSku();
        this.productName = d.getProductName();
        this.quantity = d.getQuantity();
        this.unitaryPrice = d.getUnitaryPrice();
        this.productDiscount = d.getProductDiscount();
        this.total = d.getTotal();
    }

}
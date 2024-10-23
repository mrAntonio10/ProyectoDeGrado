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
public class AllDetailInfoDto {
    private String productCode;
    private String productName;
    private BigInteger quantity;
    private BigDecimal unitaryPrice;
    private BigDecimal productDiscount;
    private BigDecimal total;

    private Document doc;

    public AllDetailInfoDto(OrderDetail o) {
        this.productCode = o.getProductCode();
        this.productName = o.getProduct().getName();
        this.quantity = o.getQuantity();
        this.unitaryPrice = o.getUnitaryPrice();
        this.productDiscount = o.getDiscount();
        this.total = o.getTotalPrice();
        this.doc = o.getDocument();
    }

}
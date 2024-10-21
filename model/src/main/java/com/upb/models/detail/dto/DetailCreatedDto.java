package com.upb.models.detail.dto;

import com.upb.models.detail.OrderDetail;
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

    public DetailCreatedDto(OrderDetail od) {
        this.productName = od.getProduct().getName();
        this.quantity = od.getQuantity();
        this.totalPrice = od.getTotalPrice();
    }

}

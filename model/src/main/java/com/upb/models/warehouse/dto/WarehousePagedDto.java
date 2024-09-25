package com.upb.models.warehouse.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class WarehousePagedDto {
    private String id;
    private String productName;
    private String category;
    private BigInteger stock;
    private BigDecimal unitaryCost;
    private BigInteger max;
    private BigInteger min;


    public WarehousePagedDto(Warehouse w) {
        this.id = w.getId();
        this.productName = w.getProduct().getName();
        this.category = w.getProduct().getCategory();
        this.stock = w.getStock();
        this.unitaryCost = w.getUnitaryCost();
        this.max = w.getMaxProduct();
        this.min = w.getMinProduct();
    }



}

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
public class WarehousePageableProductsDto {
    private String idProduct;
    private String productName;
    private BigDecimal unitaryCost;
    private String sku;

    public WarehousePageableProductsDto(Warehouse w) {
        this.idProduct = w.getProduct().getId();
        this.productName = productNameStructure(w.getProduct().getName(), w.getProduct().getBeverageFormat());
        this.unitaryCost = w.getUnitaryCost();
        this.sku = w.getProduct().getSku();
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }

}
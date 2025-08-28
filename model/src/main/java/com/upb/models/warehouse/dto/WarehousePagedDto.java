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
    private String branchOfficeName;
    private Boolean stockState;
    private String sku;


    public WarehousePagedDto(Warehouse w) {
        this.id = w.getId();
        this.productName = this.productNameStructure(w.getProduct().getName(), w.getProduct().getBeverageFormat());
        this.category = w.getProduct().getCategory();
        this.stock = w.getStock();
        this.unitaryCost = w.getUnitaryCost();
        this.max = w.getMaxProduct();
        this.min = w.getMinProduct();
        this.branchOfficeName = w.getBranchOffice().getName();
        this.stockState = isStockStateValid(w.getStock(), w.getMinProduct());
        this.sku = w.getProduct().getSku();
    }

    private Boolean isStockStateValid(BigInteger stock, BigInteger min) {
        return (stock.compareTo(min) == 1) ? true : false;
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }

}

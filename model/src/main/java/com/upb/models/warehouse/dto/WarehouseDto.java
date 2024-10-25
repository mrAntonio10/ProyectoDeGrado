package com.upb.models.warehouse.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class WarehouseDto {
    private String id;
    private String productName;
    private String productCode;
    private BigInteger stock;
    private String state;


    public WarehouseDto(Warehouse w) {
        this.id = w.getId();
        this.productName = this.productNameStructure(w.getProduct().getName(), w.getProduct().getBeverageFormat());
        this.stock = w.getStock();
        this.state = w.getState();
        this.productCode = getProductCode();
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }

}

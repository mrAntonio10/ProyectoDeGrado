package com.upb.models.warehouse.dto;

import com.upb.models.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class WarehouseStateDto {
    private String id;
    private String productName;
    private String state;


    public WarehouseStateDto(Warehouse w) {
        this.id = w.getId();
        this.productName = w.getProduct().getName();
        this.state = w.getState();
    }

}

package com.upb.models.product.dto;

import com.upb.models.operation.dto.OperationDto;
import com.upb.models.permission.Permission;
import com.upb.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private String category;
    private Boolean state;

    public ProductDto(Product p) {
        this.id = p.getId();
        this.name = p.getName();
        this.category = p.getCategory();
        this.state = p.getState();
    }
}

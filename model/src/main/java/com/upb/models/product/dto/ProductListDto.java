package com.upb.models.product.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ProductListDto {
    private String id;
    private String name;
    private String category;

    public ProductListDto(Product p) {
        this.id = p.getId();
        this.name = this.productNameStructure(p.getName(), p.getBeverageFormat());
        this.category = p.getCategory();
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }
}

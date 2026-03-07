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
public class ProductWithImageDto {
    private String id;
    private String name;
    private String category;
    private String photo;

    private String productName;
    private String beverageFormat;

    private java.math.BigInteger stock;
    private java.math.BigDecimal unitaryCost;

    public ProductWithImageDto(Product p) {
        this.id = p.getId();
        this.name = this.productNameStructure(p.getName(), p.getBeverageFormat());
        this.category = p.getCategory();
        this.photo = p.getPhoto();

        this.productName = p.getName();
        this.beverageFormat = p.getBeverageFormat();
    }

    public ProductWithImageDto(Product p, java.math.BigInteger stock, java.math.BigDecimal unitaryCost) {
        this.id = p.getId();
        this.name = this.productNameStructure(p.getName(), p.getBeverageFormat());
        this.category = p.getCategory();
        this.photo = p.getPhoto();

        this.productName = p.getName();
        this.beverageFormat = p.getBeverageFormat();
        
        this.stock = stock;
        this.unitaryCost = unitaryCost;
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }
}

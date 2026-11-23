package com.upb.models.product.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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

    private BigInteger stock;
    private BigDecimal unitaryCost;

    // Rekids Garment Fields
    private String sku;
    private String size;
    private String color;
    private String gender;
    private String brand;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String region;
    private String externalId;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal finalPrice;
    private String discountApprovedBy;
    private Long discountApprovedDate;
    private Boolean isNew;
    private Long lastSyncDate;
    private List<String> eventNames;

    public ProductWithImageDto(Product p) {
        this.id = p.getId();
        this.name = this.productNameStructure(p.getName(), p.getBeverageFormat());
        this.category = p.getCategory();
        this.photo = p.getPhoto();

        this.productName = p.getName();
        this.beverageFormat = p.getBeverageFormat();
        this.populateRekidsFields(p, BigInteger.ONE, p.getSalePrice());
    }

    public ProductWithImageDto(Product p, BigInteger stock, BigDecimal unitaryCost) {
        this.id = p.getId();
        this.name = this.productNameStructure(p.getName(), p.getBeverageFormat());
        this.category = p.getCategory();
        this.photo = p.getPhoto();

        this.productName = p.getName();
        this.beverageFormat = p.getBeverageFormat();

        this.stock = stock != null ? stock : BigInteger.ONE;
        this.unitaryCost = unitaryCost != null ? unitaryCost : (p.getFinalPrice() != null ? p.getFinalPrice() : p.getSalePrice());
        this.populateRekidsFields(p, this.stock, this.unitaryCost);
    }

    private void populateRekidsFields(Product p, BigInteger stockVal, BigDecimal priceVal) {
        this.sku = p.getSku();
        this.size = p.getSize();
        this.color = p.getColor();
        this.gender = p.getGender();
        this.brand = p.getBrand();
        this.purchasePrice = p.getPurchasePrice();
        this.salePrice = p.getSalePrice() != null ? p.getSalePrice() : priceVal;
        this.region = p.getRegion();
        this.externalId = p.getExternalId();
        this.discountType = p.getDiscountType();
        this.discountValue = p.getDiscountValue();
        this.finalPrice = p.getFinalPrice() != null ? p.getFinalPrice() : this.salePrice;
        this.discountApprovedBy = p.getDiscountApprovedBy();
        this.discountApprovedDate = p.getDiscountApprovedDate();
        this.isNew = p.getIsNew();
        this.lastSyncDate = p.getLastSyncDate();
        if (this.unitaryCost == null) {
            this.unitaryCost = this.finalPrice;
        }
        if (this.stock == null) {
            this.stock = stockVal != null ? stockVal : BigInteger.ONE;
        }
    }

    private String productNameStructure(String name, String beverageFormat){
        return (!StringUtil.isNullOrEmpty(beverageFormat) ? name + " - " + beverageFormat : name);
    }
}

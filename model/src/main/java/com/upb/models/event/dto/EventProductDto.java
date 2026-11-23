package com.upb.models.event.dto;

import com.upb.models.event.EventProduct;
import com.upb.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventProductDto {
    private String id;
    private String eventId;
    private String eventName;
    private String productId;
    private String sku;
    private String productName;
    private String category;
    private String size;
    private String color;
    private String gender;
    private String brand;
    private String region;
    private String photo;
    private BigDecimal originalPrice;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal finalPrice;
    private String approvedByAdminId;
    private String approvedByAdminName;
    private Long approvedDate;

    public EventProductDto(EventProduct ep) {
        this.id = ep.getId();
        if (ep.getEvent() != null) {
            this.eventId = ep.getEvent().getId();
            this.eventName = ep.getEvent().getName();
        }
        Product p = ep.getProduct();
        if (p != null) {
            this.productId = p.getId();
            this.sku = p.getSku();
            this.productName = p.getName();
            this.category = p.getCategory();
            this.size = p.getSize();
            this.color = p.getColor();
            this.gender = p.getGender();
            this.brand = p.getBrand();
            this.region = p.getRegion();
            this.photo = p.getPhoto();
        }
        this.originalPrice = ep.getOriginalPrice() != null ? ep.getOriginalPrice() : (p != null ? p.getSalePrice() : BigDecimal.ZERO);
        this.discountType = ep.getDiscountType() != null ? ep.getDiscountType() : (p != null ? p.getDiscountType() : "NONE");
        this.discountValue = ep.getDiscountValue() != null ? ep.getDiscountValue() : (p != null ? p.getDiscountValue() : BigDecimal.ZERO);
        this.finalPrice = ep.getFinalPrice() != null ? ep.getFinalPrice() : (p != null && p.getFinalPrice() != null ? p.getFinalPrice() : this.originalPrice);
        this.approvedByAdminId = ep.getApprovedByAdminId();
        this.approvedByAdminName = ep.getApprovedByAdminName() != null ? ep.getApprovedByAdminName() : (p != null ? p.getDiscountApprovedBy() : null);
        this.approvedDate = ep.getApprovedDate() != null ? ep.getApprovedDate() : (p != null ? p.getDiscountApprovedDate() : null);
    }
}

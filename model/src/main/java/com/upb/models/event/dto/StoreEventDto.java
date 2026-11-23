package com.upb.models.event.dto;

import com.upb.models.event.StoreEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreEventDto {
    private String id;
    private String name;
    private String code;
    private String description;
    private Boolean isDefault;
    private String discountType;
    private BigDecimal discountValue;
    private String approvedByAdminId;
    private String approvedByAdminName;
    private Long approvedDate;
    private Long startDate;
    private Long endDate;
    private String state;
    private int productCount;
    private List<EventProductDto> products;

    public StoreEventDto(StoreEvent event, List<EventProductDto> products) {
        this.id = event.getId();
        this.name = event.getName();
        this.code = event.getCode();
        this.description = event.getDescription();
        this.isDefault = event.getIsDefault();
        this.discountType = event.getDiscountType();
        this.discountValue = event.getDiscountValue();
        this.approvedByAdminId = event.getApprovedByAdminId();
        this.approvedByAdminName = event.getApprovedByAdminName();
        this.approvedDate = event.getApprovedDate();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.state = event.getState();
        this.products = products != null ? products : new ArrayList<>();
        this.productCount = this.products.size();
    }
}

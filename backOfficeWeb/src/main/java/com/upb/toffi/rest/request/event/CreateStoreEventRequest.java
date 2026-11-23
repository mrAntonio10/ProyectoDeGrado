package com.upb.toffi.rest.request.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreEventRequest {
    private String name;
    private String description;
    private Long startDate;
    private Long endDate;
    private List<String> productIds;
    private String discountType;
    private BigDecimal discountValue;
}

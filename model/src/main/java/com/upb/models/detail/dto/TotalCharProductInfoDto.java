package com.upb.models.detail.dto;

import com.upb.models.detail.OrderDetail;
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
public class TotalCharProductInfoDto {
    private String branchOfficeName;
    private String productName;
    private BigInteger totalQuantity;
}

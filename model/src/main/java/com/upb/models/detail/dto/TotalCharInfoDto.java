package com.upb.models.detail.dto;

import com.upb.models.detail.OrderDetail;
import com.upb.models.document.Document;
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
public class TotalCharInfoDto {
    private String branchOfficeName;
    private BigDecimal totalPrice;
    private BigInteger totalQuantity;
}

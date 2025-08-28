package com.upb.models.document.dto;

import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.detail.dto.DetailCreatedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class DocumentCreatedDto {
    private String id;
    private String state;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String deliveryInformation;

    private List<DetailCreatedDto> detailList;

}

package com.upb.toffi.rest.request.document;

import com.upb.models.detail.dto.DetailListRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class CreateDocumentRequest {
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String deliveryInformation;

    private List<DetailListRequest> detailList;

}

package com.upb.toffi.rest.request.document;

import com.upb.models.detail.dto.DetailListRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateDocumentRequest {
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String deliveryInformation;

    private String clientName;
    private String clientPhone;
    private String channel;
    private String region;

    private List<DetailListRequest> detailList;

    private Boolean isInvoiced;
}

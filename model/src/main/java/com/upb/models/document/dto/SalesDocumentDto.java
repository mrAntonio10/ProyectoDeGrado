package com.upb.models.document.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.detail.dto.DetailCreatedDto;
import com.upb.models.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class SalesDocumentDto {
    private String id;
    private LocalDateTime salesDate;
    private String client;
    private String paymentMethod;
    private BigDecimal totalPrice;

    public SalesDocumentDto(Document d) {
        this.id = d.getId();
        this.salesDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getDeliveryDate()), ZoneId.of("America/La_Paz")).toLocalDateTime();
        this.client = this.getClientNames(d.getDeliveryInformation());
        this.paymentMethod = this.getPaymentMethod(d.getPaymentMethod());
        this.totalPrice = d.getTotalPrice();
    }

    private String getPaymentMethod(String paymentMethod) {
        switch(paymentMethod) {
            case "QR":
                return "Qr";
            case "EFECTIVO":
                return "Efectivo";
            case "TARJETA":
                return "Tarjeta";
            default:
                return "Método no reconocido";
        }
    }

    private String getClientNames(String name) {
        return(!StringUtil.isNullOrEmpty(name) ? name : "");
    }

}

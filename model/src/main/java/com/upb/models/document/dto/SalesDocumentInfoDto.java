package com.upb.models.document.dto;

import ch.qos.logback.core.util.StringUtil;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.detail.dto.DetailInfoDto;
import com.upb.models.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class SalesDocumentInfoDto {
    private String id;

    private String salesDate;
    private String userPOS;
    private String paymentMethod;
    private String client;

    private BigDecimal subTotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;

    private List<DetailInfoDto> detailInfoList;
    public SalesDocumentInfoDto(Document d) {
        this.id = d.getId();

        this.userPOS = d.getSalesUser().getName() +" "+ d.getSalesUser().getLastname();
        this.salesDate = this.getDateTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getDeliveryDate()), ZoneId.of("America/La_Paz")).toLocalDateTime());
        this.paymentMethod = this.getPaymentMethod(d.getPaymentMethod());
        this.client = this.getClientNames(d.getDeliveryInformation());

        this.totalDiscount = d.getTotalDiscount();
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

    private String getDateTime(LocalDateTime dt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MMMM-yyyy HH:mm:ss", new Locale("es", "ES"));

        return dt.format(formatter).substring(0, 1).toUpperCase() + dt.format(formatter).substring(1).toLowerCase();
    }

}
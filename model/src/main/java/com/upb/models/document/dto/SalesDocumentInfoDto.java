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
    private String state;
    private String clientName;
    private String clientPhone;
    private String channel;
    private String region;

    public SalesDocumentInfoDto(Document d) {
        this.id = d.getId();

        this.userPOS = d.getSalesUser() != null ? (d.getSalesUser().getName() +" "+ d.getSalesUser().getLastname()) : "ONLINE (Rekids)";
        this.salesDate = this.getDateTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getDeliveryDate()), ZoneId.of("America/La_Paz")).toLocalDateTime());
        this.paymentMethod = this.getPaymentMethod(d.getPaymentMethod());
        this.client = !StringUtil.isNullOrEmpty(d.getClientName()) ? d.getClientName() : this.getClientNames(d.getDeliveryInformation());
        this.clientName = d.getClientName();
        this.clientPhone = d.getClientPhone();
        this.channel = !StringUtil.isNullOrEmpty(d.getChannel()) ? d.getChannel() : "ONLINE";
        this.region = d.getRegion();

        this.totalDiscount = d.getTotalDiscount();
        this.totalPrice = d.getTotalPrice();
        this.state = (d.getState().equals("ACEPTADO")) ? "Aceptado" : "Eliminado";
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
package com.upb.models.document.dto;

import ch.qos.logback.core.util.StringUtil;
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
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminSalesDocumentDto {
    private String id;
    private String salesDate;
    private String client;
    private String paymentMethod;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;
    private String state;
    private String sellerName;
    private String invoiceStatus;

    public AdminSalesDocumentDto(Document d) {
        this.id = d.getId();
        this.salesDate = this.getDateTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getDeliveryDate()), ZoneId.of("America/La_Paz")).toLocalDateTime());
        this.client = this.getClientNames(d.getDeliveryInformation());
        this.paymentMethod = this.getPaymentMethod(d.getPaymentMethod());
        this.totalDiscount = d.getTotalDiscount();
        this.totalPrice = d.getTotalPrice();
        this.state = (d.getState().equals("ACEPTADO")) ? "Aceptado" : "Eliminado";
        
        if (d.getSalesUser() != null) {
            this.sellerName = d.getSalesUser().getName() + " " + d.getSalesUser().getLastname();
        } else {
            this.sellerName = "Desconocido";
        }
        
        this.invoiceStatus = (d.getIsInvoiced() != null && d.getIsInvoiced()) ? "FACTURADAS" : "NO FACTURADAS";
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

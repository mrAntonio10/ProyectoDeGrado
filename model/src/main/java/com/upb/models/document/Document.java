package com.upb.models.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DOCUMENT")
public class Document implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @Basic
    @Column(name = "TYPE", length = 15,nullable = false)
    private String type;

    @Basic
    @Column(name = "STATE", length = 15,nullable = false)
    private String state;

    @Basic
    @Column(name = "PAYMENT_METHOD", length = 10)
    private String paymentMethod;

    @Basic
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    @Basic
    @Column(name = "TOTAL_DISCOUNT")
    private BigDecimal totalDiscount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SALES_USER", referencedColumnName = "ID")
    private User salesUser;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_BRANCH_OFFICE", referencedColumnName = "ID")
    private BranchOffice branchOfficeInfo;

    @Column(name = "DELIVERY_DATE")
    private Long deliveryDate;

    @Basic
    @Column(name = "DELIVERY_INFORMATION", length = 60)
    private String deliveryInformation;

    @Basic
    @Column(name = "REASON")
    private String reason;
}
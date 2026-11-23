package com.upb.models.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STORE_EVENT")
public class StoreEvent implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @Column(name = "NAME", length = 80, nullable = false)
    private String name;

    @Column(name = "CODE", length = 40, nullable = false)
    private String code;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    @Column(name = "DISCOUNT_TYPE", length = 20)
    private String discountType;

    @Column(name = "DISCOUNT_VALUE")
    private BigDecimal discountValue;

    @Column(name = "APPROVED_BY_ADMIN_ID", length = 60)
    private String approvedByAdminId;

    @Column(name = "APPROVED_BY_ADMIN_NAME", length = 120)
    private String approvedByAdminName;

    @Column(name = "APPROVED_DATE")
    private Long approvedDate;

    @Column(name = "START_DATE")
    private Long startDate;

    @Column(name = "END_DATE")
    private Long endDate;

    @Column(name = "STATE", length = 20, nullable = false)
    private String state;
}

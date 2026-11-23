package com.upb.models.event;

import com.upb.models.product.Product;
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
@Table(name = "EVENT_PRODUCT")
public class EventProduct implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EVENT", referencedColumnName = "ID", nullable = false)
    private StoreEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUCT", referencedColumnName = "ID", nullable = false)
    private Product product;

    @Column(name = "DISCOUNT_TYPE", length = 20)
    private String discountType;

    @Column(name = "DISCOUNT_VALUE")
    private BigDecimal discountValue;

    @Column(name = "ORIGINAL_PRICE")
    private BigDecimal originalPrice;

    @Column(name = "FINAL_PRICE")
    private BigDecimal finalPrice;

    @Column(name = "APPROVED_BY_ADMIN_ID", length = 60)
    private String approvedByAdminId;

    @Column(name = "APPROVED_BY_ADMIN_NAME", length = 120)
    private String approvedByAdminName;

    @Column(name = "APPROVED_DATE")
    private Long approvedDate;

    @Column(name = "STATE")
    private Boolean state;
}

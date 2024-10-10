package com.upb.models.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.document.Document;
import com.upb.models.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDER_DETAIL")
public class OrderDetail implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @Basic
    @Column(name = "QUANTITY", nullable = false)
    private BigInteger quantity;

    @Basic
    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Basic
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "ID_PRODUCT", referencedColumnName = "ID")
    private Product productInfo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOCUMENT", referencedColumnName = "ID", nullable=false)
    public Document document;
}
package com.upb.models.transferTicket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = { "ticket", "product" })
@ToString(exclude = { "ticket", "product" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TRANSFER_DETAIL")
public class TransferDetail implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TICKET", referencedColumnName = "ID", nullable = false)
    private TransferTicket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUCT", referencedColumnName = "ID", nullable = false)
    private Product product;

    @Column(name = "QUANTITY_SHIPPED", nullable = false)
    private BigInteger quantityShipped;

    // Cuántos llegaron bien, puede ser null hasta que se procese
    @Column(name = "QUANTITY_RECEIVED")
    private BigInteger quantityReceived;

    // Cuántos llegaron dañados
    @Column(name = "QUANTITY_DAMAGED")
    private BigInteger quantityDamaged;
}

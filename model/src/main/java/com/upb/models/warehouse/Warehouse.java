package com.upb.models.warehouse;

import com.upb.models.branchOffice.BranchOffice;
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
@Table(name = "WAREHOUSE")
public class Warehouse implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_PRODUCT", referencedColumnName = "ID", nullable=false)
    private Product product;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "STOCK")
    private BigInteger stock;

    @Column(name = "UNITARY_COST")
    private BigDecimal unitaryCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_BRANCHOFFICE", referencedColumnName = "ID", nullable=false)
    private BranchOffice branchOffice;

    @Column(name = "STATE", nullable = false, length = 20)
    private String state;

    @Column(name = "MAX_PRODUCT")
    private BigInteger maxProduct;
    
    @Column(name = "MIN_PRODUCT")
    private BigInteger minProduct;

}

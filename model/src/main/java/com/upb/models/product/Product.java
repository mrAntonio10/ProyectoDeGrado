package com.upb.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.enterprise.Enterprise;
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
@Table(name = "PRODUCT")
public class Product implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 60, nullable = false)
    private String name;

    @Column(name = "CATEGORY", length = 30, nullable = false)
    private String category;

    /*Segmentos de consumibles*/
    @Column(name = "BEVERAGE_FORMAT", length = 30)
    private String beverageFormat;

    /* Segmentos de prendas */
    @Column(name = "CLOTHING_SIZE", length = 15)
    private String size;

    @Column(name = "COLOR", length = 30)
    private String color;

    @Column(name = "GENDER", length = 30)
    private String gender;

    @Column(name = "BRAND", length = 60)
    private String brand;

    @Column(name = "PURCHASE_PRICE")
    private BigDecimal purchasePrice;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "RECEPTION_DATE")
    private Long receptionDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_SUPPLIER", referencedColumnName = "ID")
    private com.upb.models.supplier.Supplier supplier;

    @Column(name = "COMMISSION_PERCENTAGE")
    private BigDecimal commissionPercentage;

    /* Datos Generales */
    @Column(name = "STATE")
    private Boolean state;

    //ALTER TABLE product ALTER COLUMN sku TYPE varchar(50);
    @Column(name = "SKU", length = 50)
    private String sku;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_ENTERPRISE", referencedColumnName = "ID", nullable=true)
    private Enterprise enterprise;

    @Column(name = "PHOTO", columnDefinition = "TEXT")
    private String photo;

    /* Campos Rekids - Tienda Online de Prendas y Sincronización Externa (FeignClient) */
    @Column(name = "REGION", length = 30)
    private String region;

    @Column(name = "EXTERNAL_ID", length = 80)
    private String externalId;

    @Column(name = "DISCOUNT_TYPE", length = 20)
    private String discountType;

    @Column(name = "DISCOUNT_VALUE")
    private BigDecimal discountValue;

    @Column(name = "FINAL_PRICE")
    private BigDecimal finalPrice;

    @Column(name = "DISCOUNT_APPROVED_BY", length = 120)
    private String discountApprovedBy;

    @Column(name = "DISCOUNT_APPROVED_DATE")
    private Long discountApprovedDate;

    @Column(name = "IS_NEW")
    private Boolean isNew;

    @Column(name = "LAST_SYNC_DATE")
    private Long lastSyncDate;

}

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT")
public class Product implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 60, nullable = false)
    private String name;

    @Column(name = "CATEGORY", length = 30, nullable = false)
    private String category;

    @Column(name = "BEVERAGE_FORMAT", length = 30)
    private String beverageFormat;

    @Column(name = "STATE")
    private Boolean state;

    @Column(name = "SKU", length = 6)
    private String sku;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_ENTERPRISE", referencedColumnName = "ID", nullable=false)
    private Enterprise enterprise;

}

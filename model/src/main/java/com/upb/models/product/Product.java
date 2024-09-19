package com.upb.models.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "ID")
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

}

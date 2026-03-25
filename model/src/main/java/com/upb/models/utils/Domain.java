package com.upb.models.utils;

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
@Table(name = "DOMAIN")
public class Domain implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @Column(name = "DOMAIN", length = 50, nullable = false)
    private String domain;

    @Column(name = "VALUE", length = 30, nullable = false)
    private String name; // Note: mapped to VALUE

    @Column(name = "DESCRIPTION")
    private String description;

    @Builder.Default
    @Column(name = "IS_DELETED", columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_domain", referencedColumnName = "ID")
    private Domain parentDomain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTERPRISE", referencedColumnName = "ID")
    private Enterprise enterprise;

}

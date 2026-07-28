package com.upb.models.supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "SUPPLIER")
public class Supplier implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @Column(name = "CONTACT_NAME", length = 120)
    private String contactName;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "STATE", length = 20)
    private String state;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTERPRISE", referencedColumnName = "ID", nullable = false)
    public Enterprise enterprise;
}

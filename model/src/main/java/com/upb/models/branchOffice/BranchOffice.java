package com.upb.models.branchOffice;

import com.upb.models.enterprise.Enterprise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BRANCHOFFICE")
public class BranchOffice implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 120,nullable = false)
    private String name;

    @Column(name = "LOCATION", length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID", nullable=false)
    public Enterprise enterprise;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "STATE")
    private String state;

    @Column(name = "INVOICE")
    private Boolean invoice;

    @Column(name = "IN_CODE", length = 255)
    private String iNCode;

}
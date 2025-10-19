package com.upb.models.utils;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DOMAIN")
public class Domain implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "VALUE", length = 30,nullable = false)
    private String name;

   @Column(name = "DESCRIPTION")
   private String description;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_domain", referencedColumnName = "ID")
   private Domain parentDomain;

}

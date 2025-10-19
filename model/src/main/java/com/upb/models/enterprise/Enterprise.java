package com.upb.models.enterprise;

import com.upb.models.rol.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ENTERPRISE")
public class Enterprise implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @Column(name = "LOGO", length = 255)
    private String logo;

    @Column(name = "DESCRIPTION", length = 150, nullable = false)
    private String description;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false,length = 255)
    private String email;

    @Column(name = "STATE")
    private String state;

}

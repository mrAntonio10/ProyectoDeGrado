package com.upb.models.resource;

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
@Table(name = "RESOURCE")
public class Resource implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 150,nullable = false)
    private String name;

    @Column(name = "URL", length = 255,nullable = false)
    private String url;

    @Column(name = "ICON", length = 255, nullable = false)
    private String icon;

    @Column(name = "STATE")
    private Boolean state;

    @Column(name = "PRIORITY", nullable = false)
    private int priority;

    @Column(name = "DESCRIPTION", length = 150, nullable = false)
    private String description;

    @Column(name = "ID_PARENT", length = 36)
    private String idParent;

}

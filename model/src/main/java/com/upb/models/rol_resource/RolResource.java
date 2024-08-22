package com.upb.models.rol_resource;

import com.upb.models.resource.Resource;
import com.upb.models.rol.Rol;
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
@Table(name = "ROL_RESOURCE")
public class RolResource implements Serializable {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @ManyToOne
    @JoinColumn(name = "ID_ROL")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "ID_RESOURCE")
    private Resource resource;

}

package com.upb.models.permission;

import com.upb.models.operation.Operation;
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
@Table(name = "PERMISSION")
public class Permission implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RESOURCE", referencedColumnName = "ID")
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERATION", referencedColumnName = "ID")
    private Operation operation;

   @Column(name = "STATE")
   private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ROL", referencedColumnName = "ID")
    private Rol rol;

}

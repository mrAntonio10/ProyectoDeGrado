package com.upb.models.rol_resource.dto;

import com.upb.models.rol_resource.RolResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class RolResourceDto {
    private String idRol;
    private String idResource;
    private String resourceName;
    private Integer resourcePriority;
    private String url;
    private String icon;
    private String idParent;
    private List<RolResourceDto> childrenResources;

    public RolResourceDto(RolResource roleResource) {
        this.idRol = roleResource.getRol().getId();
        this.idResource = roleResource.getResource().getId();
        this.resourceName = roleResource.getResource().getName();
        this.resourcePriority = roleResource.getResource().getPriority();
        this.url = roleResource.getResource().getUrl();
        this.icon = roleResource.getResource().getIcon();
        this.idParent = roleResource.getResource().getIdParent();
    }
}

package com.upb.models.rol.dto;

import com.upb.models.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class RolForListDto {
   private String name;
   private String code;

    public RolForListDto(Rol rol) {
        this.name = rol.getName();
        this.code = rol.getId();
    }
}

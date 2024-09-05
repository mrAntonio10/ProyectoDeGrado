package com.upb.models.permission.dto;

import com.upb.models.operation.dto.OperationDto;
import com.upb.models.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ResourcePermissionDto {
    private String permissionName;


    public ResourcePermissionDto(Permission permission) {
        this.permissionName = permission.getOperation().getName();
    }
}

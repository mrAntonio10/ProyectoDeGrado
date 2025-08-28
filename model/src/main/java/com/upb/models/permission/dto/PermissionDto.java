package com.upb.models.permission.dto;

import com.upb.models.enterprise.Enterprise;
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
public class PermissionDto {
    private String idResource;
    private String resourceName;
    private OperationDto operationDto;

    private List<OperationDto> operationDtoList;

    public PermissionDto(Permission permission) {
        this.idResource = permission.getResource().getId();
        this.resourceName = permission.getResource().getName();

        this.operationDto = new OperationDto(permission.getOperation());
    }
}

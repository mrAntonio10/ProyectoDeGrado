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
public class PermissionResponseDto {
    private String idResource;
    private String resourceName;
    private List<OperationDto> operationDtoList;

    public PermissionResponseDto(PermissionDto p) {
        this.idResource = p.getIdResource();
        this.resourceName = p.getResourceName();
    }

}

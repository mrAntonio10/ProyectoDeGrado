package com.upb.models.operation.dto;

import com.upb.models.enterprise.Enterprise;
import com.upb.models.operation.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class OperationDto {
    private String id;
    private String name;
    private String description;
    private Boolean state;

    public OperationDto(Operation op) {
        this.id = op.getId();
        this.name = op.getName();
        this.description = op.getDescription();
        this.state = op.getState();
    }
}

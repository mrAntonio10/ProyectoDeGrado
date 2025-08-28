package com.upb.models.branchOffice.dto;

import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.enterprise.Enterprise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class BranchOfficeStateDto {
    private String id;
    private String name;
    private String state;

    public BranchOfficeStateDto(BranchOffice b) {
        this.id = b.getId();
        this.name = b.getName();
        this.state = b.getState();
    }
}

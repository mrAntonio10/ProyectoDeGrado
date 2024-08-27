package com.upb.models.branchOffice.dto;

import com.upb.models.branchOffice.BranchOffice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class BranchOfficeDto {
    private String id;
    private String name;
    private String location;
    private Boolean invoice;
    private String state;

    public BranchOfficeDto(BranchOffice b) {
        this.id = b.getId();
        this.name = b.getName();
        this.location = b.getLocation();
        this.invoice = b.getInvoice();
        this.state = b.getState();

    }
}

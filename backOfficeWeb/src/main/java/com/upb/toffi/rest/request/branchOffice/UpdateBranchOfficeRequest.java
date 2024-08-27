package com.upb.toffi.rest.request.branchOffice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBranchOfficeRequest {
    private String id;
    private String name;
    private String location;
    private String phoneNumber;
    private String idEnterprise;
    private Boolean invoice;
    private String inCode;
    private String state;
}



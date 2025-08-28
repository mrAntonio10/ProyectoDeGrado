package com.upb.toffi.rest.request.enterprise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnterpriseRequest {
    private String name;
    private String email;
    private String description;
    private String phoneNumber;
    private String logo;
}



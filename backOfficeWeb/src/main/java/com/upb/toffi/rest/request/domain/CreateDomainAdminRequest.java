package com.upb.toffi.rest.request.domain;

import lombok.Data;

@Data
public class CreateDomainAdminRequest {
    private String domain;
    private String name;
    private String description;
}

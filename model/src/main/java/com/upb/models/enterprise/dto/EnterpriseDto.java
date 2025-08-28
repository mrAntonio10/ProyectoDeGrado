package com.upb.models.enterprise.dto;

import com.upb.models.enterprise.Enterprise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class EnterpriseDto {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;

    public EnterpriseDto(Enterprise e) {
        this.id = e.getId();
        this.name = e.getName();
        this.email = e.getEmail();
        this.phoneNumber = e.getPhoneNumber();
    }
}

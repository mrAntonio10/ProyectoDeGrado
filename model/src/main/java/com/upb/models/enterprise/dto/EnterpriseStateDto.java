package com.upb.models.enterprise.dto;

import com.upb.models.enterprise.Enterprise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class EnterpriseStateDto {
    private String id;
    private String name;
    private String state;

    public EnterpriseStateDto(String id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public EnterpriseStateDto(Enterprise e) {
        this.id = e.getId();
        this.name = e.getName();
        this.state = e.getState();
    }
}

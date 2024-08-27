package com.upb.models.enterprise.dto;

import com.upb.models.enterprise.Enterprise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class EnterpriseStateDto {
    private String id;
    private String name;
    private String state;

    public EnterpriseStateDto(Enterprise e) {
        this.id = e.getId();
        this.name = e.getName();
        this.state = e.getState();
    }
}

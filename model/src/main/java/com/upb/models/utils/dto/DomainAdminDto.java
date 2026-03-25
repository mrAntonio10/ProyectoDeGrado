package com.upb.models.utils.dto;

import com.upb.models.utils.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainAdminDto {
    private String id;
    private String domain;
    private String name;
    private String description;

    public DomainAdminDto(Domain d) {
        this.id = d.getId();
        this.domain = d.getDomain();
        this.name = d.getName();
        this.description = d.getDescription();
    }
}

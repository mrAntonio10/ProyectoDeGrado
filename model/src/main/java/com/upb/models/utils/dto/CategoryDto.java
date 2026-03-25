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
public class CategoryDto {
    private String id;
    private String name;
    private String description;

    public CategoryDto(Domain domain) {
        this.id = domain.getId();
        this.name = domain.getName();
        this.description = domain.getDescription();
    }
}

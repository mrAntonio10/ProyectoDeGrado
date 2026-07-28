package com.upb.toffi.rest.request.domain;

import lombok.Data;

@Data
public class CreateCategoryRequest {
    private String name;
    private String description;
}

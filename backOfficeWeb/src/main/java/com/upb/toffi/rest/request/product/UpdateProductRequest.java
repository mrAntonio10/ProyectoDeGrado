package com.upb.toffi.rest.request.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String id;
    private String name;
    private String category;
    private String beverageFormat;
}

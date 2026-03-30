package com.upb.toffi.rest.request.supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {
    private String name;
    private String contactName;
    private String phone;
}

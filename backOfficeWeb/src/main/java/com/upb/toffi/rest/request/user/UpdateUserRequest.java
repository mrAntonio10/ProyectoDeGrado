package com.upb.toffi.rest.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String id;
    private String name;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String password;
    private String idBranchOffice;
    private String idRol;
    private String state;
}



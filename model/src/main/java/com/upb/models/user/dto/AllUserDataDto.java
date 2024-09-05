package com.upb.models.user.dto;

import com.upb.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class AllUserDataDto {
    private String id;
    private String name;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String rol;
    private String idRol;
    private String state;


    public AllUserDataDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.rol = user.getRol().getName();
        this.idRol = user.getRol().getId();
        this.state = user.getState();
    }
}

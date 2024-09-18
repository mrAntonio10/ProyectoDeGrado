package com.upb.models.user.dto;

import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
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
    private String idEnterprise;
    private String idBranchOffice;
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

    public AllUserDataDto(User_BranchOffice ub) {
        this.id = ub.getUser().getId();
        this.name = ub.getUser().getName();
        this.lastname = ub.getUser().getLastname();
        this.email = ub.getUser().getEmail();
        this.phoneNumber = ub.getUser().getPhoneNumber();
        this.rol = ub.getUser().getRol().getName();
        this.idRol = ub.getUser().getRol().getId();
        this.state = ub.getUser().getState();
        this.idEnterprise = ub.getBranchOffice().getEnterprise().getId();
        this.idBranchOffice = ub.getBranchOffice().getId();
    }
}

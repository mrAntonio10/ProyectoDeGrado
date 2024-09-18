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
public class UserDto {
    private String id;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String rol;
    private String state;
    private String branchOfficeName;


    public UserDto(User user) {
        this.id = user.getId();
        this.fullname = user.getName() +" "+ user.getLastname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.rol = user.getRol().getName();
        this.state = user.getState();
    }

    public UserDto(User_BranchOffice ub) {
        this.id = ub.getUser().getId();
        this.fullname = ub.getUser().getName() +" "+ ub.getUser().getLastname();
        this.email = ub.getUser().getEmail();
        this.phoneNumber = ub.getUser().getPhoneNumber();
        this.rol = ub.getUser().getRol().getName();
        this.state = ub.getUser().getState();
        this.branchOfficeName = ub.getBranchOffice().getName();

    }
}

package com.upb.models.user;

import com.upb.models.rol.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USER_MNG")
public class User implements Serializable, UserDetails {
    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 60,nullable = false)
    private String name;

    @Column(name = "LASTNAME", length = 60,nullable = false)
    private String lastname;

    @Column(name = "PASSWORD", length = 60, nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false,length = 255)
    private String email;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROL", referencedColumnName = "ID")
    private Rol rol;

    @Column(name = "STATE")
    private String state;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
            list.add(new SimpleGrantedAuthority(rol.getName()));

            return list;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}

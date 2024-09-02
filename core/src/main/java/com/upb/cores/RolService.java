package com.upb.cores;

import com.upb.models.permission.dto.PermissionDto;
import com.upb.models.rol.Rol;
import com.upb.models.rol.dto.RolForListDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RolService {
   List<RolForListDto> getRolByAuth(Authentication authentication);

}

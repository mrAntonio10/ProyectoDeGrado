package com.upb.cores;

import com.upb.models.permission.Permission;
import com.upb.models.permission.dto.PermissionDto;
import com.upb.models.permission.dto.ResourcePermissionDto;
import com.upb.models.rol_resource.dto.RolResourceDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PermissionService {
   List<PermissionDto> getPermissionsListByAuthenticationIdRol(Authentication authentication, String idRol);

   List<ResourcePermissionDto> getPermissionsByAuthenticationAndResourceUrl(Authentication authentication, String resourceUrl);

   Permission getPermissionById(String idPermission);

   String modifyPermissionState(String idPermisison, Boolean state);
}

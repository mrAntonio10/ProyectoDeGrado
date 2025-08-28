package com.upb.cores;

import com.upb.models.rol_resource.dto.RolResourceDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResourceService {
   List<RolResourceDto> getResourceListByAuthenticationIdRol(Authentication authentication);
}

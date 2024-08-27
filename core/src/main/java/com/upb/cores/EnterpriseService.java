package com.upb.cores;

import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.models.rol_resource.dto.RolResourceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EnterpriseService {
   Page<EnterpriseDto> getEnterprisePageable(String name, Pageable pageable);

   EnterpriseDto createEnterprise(String name, String email, String phoneNumber, String logo, String description);
   EnterpriseDto updateEnterprise(String id, String name, String email, String phoneNumber, String logo, String description, String state);

   EnterpriseStateDto deleteEnterpriseById(String id);

   Enterprise getEnterpriseById(String idEnterprise);

}

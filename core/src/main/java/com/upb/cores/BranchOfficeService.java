package com.upb.cores;

import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BranchOfficeService {
   Page<BranchOfficeDto> getBranchOfficePageable(String name, String idEnterprise, Authentication auth, Pageable pageable);
   BranchOfficeDto createBranchOffice(String name, String location, String phoneNumber, String idEnterprise, Boolean invoice, String iNCode);
   BranchOfficeDto updateBranchOffice(String id, String name, String location, String phoneNumber, String state, Boolean invoice, String iNCode, String idEnterprise);
   BranchOfficeStateDto deleteBranchOfficeById(String id);
   BranchOffice getBranchOfficeById(String idBranchOffice);
   List<BranchOfficeStateDto> getBranchOfficeListByIdEnterprise(String idEnterprise);

}

package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.EnterpriseService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.BranchOfficeRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BranchOfficeServiceImpl implements BranchOfficeService {
    private final EnterpriseService enterpriseService;
    private final BranchOfficeRepository branchOfficeRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BranchOfficeDto> getBranchOfficePageable(String name, String idEnterprise, Authentication auth, Pageable pageable) {
        name = !StringUtil.isNullOrEmpty(name) ? "%" +name.toUpperCase()+ "%" : null;
        idEnterprise = !StringUtil.isNullOrEmpty(idEnterprise) ? idEnterprise : null;

        String idRol = auth.getAuthorities().stream().toList().get(0).toString();
        User user = (User) auth.getPrincipal();

        List<User_BranchOffice> ub = userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(), idRol);

        if(!ub.isEmpty()) {
            return branchOfficeRepository.getBranchOfficeByIdEnterprisePageable(ub.get(0).getBranchOffice().getEnterprise().getId(), name, pageable);
        } else {
            return branchOfficeRepository.getBranchOfficePageableForRoot(idEnterprise, name, pageable);
        }
    }

    @Override
    @Transactional()
    public BranchOfficeDto createBranchOffice(String name, String location, String phoneNumber, String idEnterprise, Boolean invoice, String iNCode) {
        StringUtilMod.notNullStringMaxLength(name, 120,"nombre");
        StringUtilMod.notNullNumberMatcherMaxLength(phoneNumber, 20,"número celular");

        if(invoice) {
            StringUtilMod.throwStringIsNullOrEmpty(iNCode, "Código - Impuestos Nacional");
        } else {
            iNCode = null;
        }

        Enterprise enterprise = enterpriseService.getEnterpriseById(idEnterprise);

        BranchOffice b = BranchOffice.builder()
                .name(name)
                .location(location)
                .phoneNumber(phoneNumber)
                .enterprise(enterprise)
                .invoice(invoice)
                .inCode(iNCode)
                .state("ACTIVE")
                .build();

        branchOfficeRepository.save(b);

        return new BranchOfficeDto(b);
    }

    @Override
    @Transactional()
    public BranchOfficeDto updateBranchOffice(String id, String name, String location, String phoneNumber, String state, Boolean invoice, String iNCode, String idEnterprise) {
        StringUtilMod.notNullStringMaxLength(name, 120,"nombre");
        StringUtilMod.notNullNumberMatcherMaxLength(phoneNumber, 20,"número celular");

        if(invoice) {
            StringUtilMod.throwStringIsNullOrEmpty(iNCode, "Código - Impuestos Nacional");
        } else {
            iNCode = null;
        }

        BranchOffice b =  branchOfficeRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes a la sucursal.")
        );

        Enterprise enterprise = enterpriseService.getEnterpriseById(idEnterprise);

        b.setName(name);
        b.setLocation(location);
        b.setPhoneNumber(phoneNumber);
        b.setState(state);
        b.setInvoice(invoice);
        b.setEnterprise(enterprise);
        b.setInCode(iNCode);

        branchOfficeRepository.save(b);

        return new BranchOfficeDto(b);
    }

    @Override
    public BranchOfficeStateDto deleteBranchOfficeById(String id) {
        BranchOffice b =  branchOfficeRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes a la sucursal.")
        );

        b.setState("DELETED");

        branchOfficeRepository.save(b);

        return new BranchOfficeStateDto(b);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchOffice getBranchOfficeById(String idBranchOffice) {
        return branchOfficeRepository.findById(idBranchOffice).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes a la sucursal.")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchOfficeStateDto> getBranchOfficeListByIdEnterprise(String idEnterprise) {
        StringUtilMod.throwStringIsNullOrEmpty(idEnterprise, "id empresa");

        return branchOfficeRepository.getBranchOfficeListByIdEnterprise(idEnterprise);
    }
}

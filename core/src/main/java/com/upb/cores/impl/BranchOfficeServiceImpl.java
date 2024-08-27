package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.EnterpriseService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.enterprise.Enterprise;
import com.upb.repositories.BranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BranchOfficeServiceImpl implements BranchOfficeService {
    private final EnterpriseService enterpriseService;
    private final BranchOfficeRepository branchOfficeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BranchOfficeDto> getBranchOfficePageable(String name, Pageable pageable) {
        name = (!StringUtil.isNullOrEmpty(name) ? "%" +name.toUpperCase()+ "%" : null);

        return branchOfficeRepository.getBranchOfficePageable(name, pageable);
    }

    @Override
    @Transactional()
    public BranchOfficeDto createBranchOffice(String name, String location, String phoneNumber, String idEnterprise, Boolean invoice, String iNCode) {
        StringUtilMod.throwStringIsNullOrEmpty(name, "nombre");

        if(invoice) {
            StringUtilMod.throwStringIsNullOrEmpty(iNCode, "Código - Impuestos Nacional");
        }

        Enterprise enterprise = enterpriseService.getEnterpriseById(idEnterprise);

        BranchOffice b = BranchOffice.builder()
                .name(name)
                .location(location)
                .phoneNumber(phoneNumber)
                .enterprise(enterprise)
                .invoice(invoice)
                .iNCode(iNCode)
                .state("ACTIVE")
                .build();

        branchOfficeRepository.save(b);

        return new BranchOfficeDto(b);
    }

    @Override
    @Transactional()
    public BranchOfficeDto updateBranchOffice(String id, String name, String location, String phoneNumber, String state, Boolean invoice, String iNCode) {
        StringUtilMod.throwStringIsNullOrEmpty(name, "nombre");

        if(invoice) {
            StringUtilMod.throwStringIsNullOrEmpty(iNCode, "Código - Impuestos Nacional");
        }

        BranchOffice b =  branchOfficeRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes a la sucursal.")
        );

        b.setName(name);
        b.setLocation(location);
        b.setPhoneNumber(phoneNumber);
        b.setState(state);
        b.setInvoice(invoice);
        b.setINCode(iNCode);

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
}

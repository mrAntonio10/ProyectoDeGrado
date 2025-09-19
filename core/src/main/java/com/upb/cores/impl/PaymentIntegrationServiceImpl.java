package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.EnterpriseService;
import com.upb.cores.PaymentIntegrationService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.models.paymentsIntegration.PaymentIntegration;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.EnterpriseRepository;
import com.upb.repositories.PaymentIntegrationRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentIntegrationServiceImpl implements PaymentIntegrationService {
    private final PaymentIntegrationRepository paymentIntegrationRepository;
    private final EnterpriseRepository enterpriseRepository;


    @Override
    public String createPayment(String user, String password, String apiKey, String idEnterprise) {
        StringUtilMod.throwStringIsNullOrEmpty(user, "user");
        StringUtilMod.throwStringIsNullOrEmpty(password, "password");
        StringUtilMod.throwStringIsNullOrEmpty(idEnterprise, "idEnterprise");

        Enterprise ent = enterpriseRepository.findById(idEnterprise).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar la empresa")
        );

        PaymentIntegration payI = PaymentIntegration.builder()
                .password(password)
                .user(user)
                .apiKey(apiKey)
                .enterprise(ent)
                .build();
        paymentIntegrationRepository.save(payI);

        return payI.getId();
    }
}

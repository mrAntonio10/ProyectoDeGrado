package com.upb.cores.impl;


import com.upb.cores.DocumentService;
import com.upb.cores.OrderDetailService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.detail.dto.DetailCreatedDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.document.Document;
import com.upb.models.document.dto.DocumentCreatedDto;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.DocumentRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final OrderDetailService orderDetailService;

    @Transactional
    @Override
    public String createDetailDocument(Authentication auth, String deliveryInformation, BigDecimal totalPrice, BigDecimal totalDiscount, String paymentMethod, List<DetailListRequest> detailList) {
        NumberUtilMod.throwNumberMaxDecimal(totalPrice, 2, "Precio total");
        NumberUtilMod.throwNumberMaxDecimal(totalDiscount, 2, "Descuento total");
        StringUtilMod.throwStringIsNullOrEmpty(paymentMethod, "Método de pago");
        StringUtilMod.canBeNull_StringMaxLength(deliveryInformation, 60, "Información de venta");

        User user = (User) auth.getPrincipal();

        User_BranchOffice ub = userBranchOfficeRepository.findUser_BranchOfficeByIdUser(user.getId()).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al usuario."));

        ZonedDateTime boliviaDateTime = ZonedDateTime.now(ZoneId.of("America/La_Paz"));

//        log.info("Fecha {}", ZonedDateTime.ofInstant(Instant.ofEpochMilli(boliviaDateTime.toInstant().toEpochMilli()), ZoneId.of("America/La_Paz")));

        Document doc = Document.builder()
                .state("ACEPTADO")
                .type("VENTA")
                .deliveryDate(boliviaDateTime.toInstant().toEpochMilli())
                .branchOfficeInfo(ub.getBranchOffice())
                .salesUser(user)
                .totalDiscount(totalDiscount)
                .totalPrice(totalPrice)
                .paymentMethod(paymentMethod)
                .deliveryInformation(deliveryInformation)
                .build();

        documentRepository.save(doc);

        orderDetailService.createDetail(doc, detailList);

        return doc.getId();
    }
}

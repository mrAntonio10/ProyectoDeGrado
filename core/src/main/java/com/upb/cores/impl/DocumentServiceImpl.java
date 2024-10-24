package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.DocumentService;
import com.upb.cores.OrderDetailService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.detail.dto.DetailInfoDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.document.Document;
import com.upb.models.document.dto.SalesDocumentInfoDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.DocumentRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final OrderDetailService orderDetailService;

    @Transactional(readOnly = true)
    @Override
    public Page<SalesUserDocumentDto> getSalesUserDocumentList(Authentication auth, String filter, LocalDate date, Pageable pageable) {
        filter = (!StringUtil.isNullOrEmpty(filter) ? "%" +filter.toUpperCase()+ "%" : null);

        User user = (User) auth.getPrincipal();
        Long start = date.atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();
        Long finish = date.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        return this.documentRepository.getSalesUserDocumetPageable(user.getId(), filter, start, finish, pageable);
    }
    @Transactional(readOnly = true)
    @Override
    public Page<SalesUserDocumentDto> getManagementSalesDocumentList(String filter, LocalDate date, String idUser, Pageable pageable) {
        filter = (!StringUtil.isNullOrEmpty(filter) ? "%" +filter.toUpperCase()+ "%" : null);

        idUser = (!StringUtil.isNullOrEmpty(idUser) ? idUser : null);

        Long start = date.atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();
        Long finish = date.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        return this.documentRepository.getManagementUserDocumetPageable(idUser, filter, start, finish, pageable);
    }

    @Override
    public SalesDocumentInfoDto getDocumentById(String idDocument) {
        List<AllDetailInfoDto> allDetailInfo = orderDetailService.getDetailDocumentInfo(idDocument);

        if(allDetailInfo.isEmpty()) {
            throw new NoSuchElementException("No fue posible recuperar los valores correspondientes al documento");
        }

        SalesDocumentInfoDto salesDocumentInfo = new SalesDocumentInfoDto(allDetailInfo.get(0).getDoc());
        salesDocumentInfo.setDetailInfoList(allDetailInfo.stream().map(DetailInfoDto::new).collect(Collectors.toList()));
        salesDocumentInfo.setSubTotal(allDetailInfo.stream()
                .map(AllDetailInfoDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return salesDocumentInfo;
    }

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

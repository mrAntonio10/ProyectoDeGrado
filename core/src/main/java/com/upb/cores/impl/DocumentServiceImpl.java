package com.upb.cores.impl;

import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.DocumentService;
import com.upb.cores.OrderDetailService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.detail.dto.DetailInfoDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.document.Document;
import com.upb.models.document.dto.SalesDocumentInfoDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.BranchOfficeRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final OrderDetailService orderDetailService;
    private final com.upb.cores.rekids.client.RekidsExternalPortalFeignClient externalPortalFeignClient;

    @Transactional(readOnly = true)
    @Override
    public Page<SalesUserDocumentDto> getSalesUserDocumentList(Authentication auth, String filter, LocalDate startDate, LocalDate endDate, String state, Pageable pageable) {
        filter = (!StringUtil.isNullOrEmpty(filter) ? "%" + filter.toUpperCase() + "%" : null);

        Long start = startDate.atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();
        Long finish = endDate.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        if (auth != null && auth.getPrincipal() instanceof User user) {
            return this.documentRepository.getSalesUserDocumetPageable(user.getId(), filter, start, finish, state.toUpperCase(), pageable);
        }
        return this.documentRepository.getManagementUserDocumetPageable(null, filter, start, finish, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SalesUserDocumentDto> getManagementSalesDocumentList(String filter, LocalDate startDate, LocalDate endDate, String idUser, Pageable pageable) {
        filter = (!StringUtil.isNullOrEmpty(filter) ? "%" + filter.toUpperCase() + "%" : null);
        idUser = (!StringUtil.isNullOrEmpty(idUser) ? idUser : null);

        Long start = startDate.atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();
        Long finish = endDate.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        return this.documentRepository.getManagementUserDocumetPageable(idUser, filter, start, finish, pageable);
    }

    @Override
    public SalesDocumentInfoDto getSalesDocumentInfoByDocumentById(String idDocument) {
        List<AllDetailInfoDto> allDetailInfo = orderDetailService.getDetailDocumentInfo(idDocument);

        if (allDetailInfo.isEmpty()) {
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
    public String createDetailDocument(Authentication auth, String deliveryInformation, BigDecimal totalPrice, BigDecimal totalDiscount, String paymentMethod, List<DetailListRequest> detailList, Boolean isInvoiced) {
        return createOnlineSaleDocument(auth, deliveryInformation, null, null, "ONLINE",
                !StringUtil.isNullOrEmpty(paymentMethod) ? paymentMethod : "QR",
                deliveryInformation, totalPrice, totalDiscount, detailList, isInvoiced);
    }

    @Transactional
    @Override
    public String createOnlineSaleDocument(Authentication auth,
                                           String clientName,
                                           String clientPhone,
                                           String region,
                                           String channel,
                                           String paymentMethod,
                                           String deliveryInformation,
                                           BigDecimal totalPrice,
                                           BigDecimal totalDiscount,
                                           List<DetailListRequest> detailList,
                                           Boolean isInvoiced) {
        NumberUtilMod.throwNumberMaxDecimal(totalPrice, 2, "Precio total");
        NumberUtilMod.throwNumberMaxDecimal(totalDiscount != null ? totalDiscount : BigDecimal.ZERO, 2, "Descuento total");

        String finalChannel = !StringUtil.isNullOrEmpty(channel) ? channel.toUpperCase() : "ONLINE";
        String finalPaymentMethod = !StringUtil.isNullOrEmpty(paymentMethod) ? paymentMethod.toUpperCase() : "QR";
        String finalRegion = normalizeRegion(region);

        String finalClientName = !StringUtil.isNullOrEmpty(clientName) ? clientName.trim() : deliveryInformation;
        if (StringUtil.isNullOrEmpty(finalClientName)) {
            throw new IllegalArgumentException("El nombre del comprador es obligatorio.");
        }

        String finalDeliveryInfo = deliveryInformation;
        if (StringUtil.isNullOrEmpty(finalDeliveryInfo)) {
            finalDeliveryInfo = finalClientName + (!StringUtil.isNullOrEmpty(clientPhone) ? " | Tel: " + clientPhone : "");
        }
        if (finalDeliveryInfo.length() > 60) {
            finalDeliveryInfo = finalDeliveryInfo.substring(0, 60);
        }

        User salesUser = null;
        BranchOffice branchOffice = null;

        if (auth != null && auth.getPrincipal() instanceof User u) {
            salesUser = u;
            Optional<User_BranchOffice> ubOpt = userBranchOfficeRepository.findUser_BranchOfficeByIdUser(u.getId());
            if (ubOpt.isPresent()) {
                branchOffice = ubOpt.get().getBranchOffice();
            }
        }

        if (branchOffice == null) {
            branchOffice = resolveBranchByRegion(finalRegion);
        }

        ZonedDateTime boliviaDateTime = ZonedDateTime.now(ZoneId.of("America/La_Paz"));

        Document doc = Document.builder()
                .state("ACEPTADO")
                .type("VENTA")
                .deliveryDate(boliviaDateTime.toInstant().toEpochMilli())
                .branchOfficeInfo(branchOffice)
                .salesUser(salesUser)
                .totalDiscount(totalDiscount != null ? totalDiscount : BigDecimal.ZERO)
                .totalPrice(totalPrice)
                .paymentMethod(finalPaymentMethod)
                .deliveryInformation(finalDeliveryInfo)
                .clientName(finalClientName)
                .clientPhone(clientPhone)
                .channel(finalChannel)
                .region(finalRegion)
                .isInvoiced(isInvoiced != null ? isInvoiced : false)
                .build();

        documentRepository.save(doc);

        if (detailList != null && !detailList.isEmpty()) {
            orderDetailService.createDetail(doc, detailList);
        }

        // Sincronizar la venta y actualización de stock con el otro portal externo via FeignClient
        java.util.Map<String, Object> externalSalePayload = new java.util.HashMap<>();
        externalSalePayload.put("documentId", doc.getId());
        externalSalePayload.put("clientName", finalClientName);
        externalSalePayload.put("clientPhone", clientPhone);
        externalSalePayload.put("region", finalRegion);
        externalSalePayload.put("channel", finalChannel);
        externalSalePayload.put("paymentMethod", finalPaymentMethod);
        externalSalePayload.put("totalPrice", totalPrice);
        externalSalePayload.put("totalDiscount", doc.getTotalDiscount());
        externalSalePayload.put("items", detailList);
        externalPortalFeignClient.syncSaleWithExternalPortal(externalSalePayload);

        return doc.getId();
    }

    private BranchOffice resolveBranchByRegion(String region) {
        List<BranchOffice> branches = branchOfficeRepository.findAll();
        if (branches.isEmpty()) {
            return null;
        }
        if (!StringUtil.isNullOrEmpty(region)) {
            String search = region.replace("_", " ").toUpperCase();
            Optional<BranchOffice> matched = branches.stream()
                    .filter(b -> !"DELETED".equalsIgnoreCase(b.getState()))
                    .filter(b -> (b.getName() != null && b.getName().toUpperCase().contains(search))
                            || (b.getLocation() != null && b.getLocation().toUpperCase().contains(search)))
                    .findFirst();
            if (matched.isPresent()) {
                return matched.get();
            }
        }
        return branches.get(0);
    }

    private String normalizeRegion(String region) {
        if (StringUtil.isNullOrEmpty(region)) return "SANTA_CRUZ";
        String upper = region.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        if (upper.contains("SANTA") || upper.equals("SCZ")) return "SANTA_CRUZ";
        if (upper.contains("PAZ") || upper.equals("LPZ")) return "LA_PAZ";
        return upper;
    }

    @Override
    public String deleteDocumentById(String idDocument) {
        Document doc = documentRepository.getDocumentByIdDocument(idDocument)
                .orElseThrow(() -> new NoSuchElementException("No fue posible recuperar el Documento con Id " + idDocument));
        doc.setState("DELETED");
        documentRepository.save(doc);

        return doc.getId();
    }
}

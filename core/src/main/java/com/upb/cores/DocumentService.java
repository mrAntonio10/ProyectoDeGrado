package com.upb.cores;

import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.document.dto.SalesDocumentInfoDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface DocumentService {
    Page<SalesUserDocumentDto> getSalesUserDocumentList(Authentication auth, String filter, LocalDate date, Pageable pageable);
    SalesDocumentInfoDto getDocumentById(String idDocument);
    String createDetailDocument(Authentication auth, String deliveryInformation, BigDecimal totalPrice, BigDecimal totalDiscount, String paymentMethod, List<DetailListRequest> detailList);
}

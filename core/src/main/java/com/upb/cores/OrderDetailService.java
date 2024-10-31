package com.upb.cores;

import com.upb.models.detail.dto.*;
import com.upb.models.document.Document;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface OrderDetailService {
    List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList);
    List<AllDetailInfoDto> getDetailDocumentInfo(String idDocument);
    List<TotalCharInfoDto> charInfoDataList(Authentication auth, LocalDate date);
    List<TotalCharProductInfoDto> charProductInfoDataList(String idBranchOffice, LocalDate date);
}

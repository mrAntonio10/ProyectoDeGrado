package com.upb.cores;

import com.upb.models.detail.dto.DetailCreatedDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderDetailService {
    List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList);
    List<AllDetailInfoDto> getDetailDocumentInfo(String idDocument);
}

package com.upb.cores;

import com.upb.models.detail.dto.DetailCreatedDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.document.Document;
import com.upb.models.document.dto.DocumentCreatedDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface OrderDetailService {
    List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList);
}

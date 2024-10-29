package com.upb.cores.impl;


import com.upb.cores.OrderDetailService;
import com.upb.models.detail.OrderDetail;
import com.upb.models.detail.dto.DetailCreatedDto;
import com.upb.models.detail.dto.DetailListRequest;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.document.Document;
import com.upb.models.product.Product;
import com.upb.repositories.OrderDetailRepository;
import com.upb.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    public List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList) {
        List<DetailCreatedDto> resp = new ArrayList<>();

        List<String> idList =detailList.stream().map(DetailListRequest::getIdProduct).toList();
        List<Product> productList = productRepository.getProductListByIdList(idList);

        if(idList.size() < productList.size()) {
            throw new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto.");
        }
        Map<String, DetailListRequest> detailsByProductId = detailList.stream()
                .collect(Collectors.toMap(DetailListRequest::getIdProduct, d -> d));

        productList.forEach(p -> {
            DetailListRequest d = detailsByProductId.get(p.getId());

            if (d != null) {
                OrderDetail od = OrderDetail.builder()
                        .discount(d.getTotalDiscount())
                        .totalPrice(d.getTotalPrice())
                        .product(p)
                        .quantity(d.getQuantity())
                        .unitaryPrice(d.getUnitaryCost())
                        .document(document)
                        .sku(d.getSku())
                        .build();

                orderDetailRepository.save(od);

                resp.add(new DetailCreatedDto(od));
            }
        });

        return resp;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AllDetailInfoDto> getDetailDocumentInfo(String idDocument) {
        return orderDetailRepository.getDetailAndDocumentInfo(idDocument);
    }
}


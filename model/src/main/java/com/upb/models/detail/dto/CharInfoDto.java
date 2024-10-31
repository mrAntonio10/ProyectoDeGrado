package com.upb.models.detail.dto;

import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.detail.OrderDetail;
import com.upb.models.document.Document;
import com.upb.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.Order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class CharInfoDto {
    private BigInteger quantity;
    private Document document;
    private BranchOffice branchOffice;
    private Product product;

    public CharInfoDto(OrderDetail d) {
        this.quantity = d.getQuantity();
        this.document = d.getDocument();
        this.branchOffice = d.getDocument().getBranchOfficeInfo();
        this.product = d.getProduct();
    }
}

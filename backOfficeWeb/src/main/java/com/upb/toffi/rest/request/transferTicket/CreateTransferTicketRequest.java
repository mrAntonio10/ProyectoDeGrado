package com.upb.toffi.rest.request.transferTicket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferTicketRequest {
    private String transferType;   // INTERNAL_TRANSFER | EXTERNAL_SUPPLY
    private String sourceId;       // ID de BranchOffice o Supplier
    private String destinationId;  // ID del BranchOffice receptor
    private String comments;
    private List<DetailItem> details;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailItem {
        private String productId;
        private Integer quantityShipped;
    }
}

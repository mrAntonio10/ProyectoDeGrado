package com.upb.models.transferTicket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetailUpdateDto {
    private String productId;
    private Integer quantityReceived;
    private Integer quantityDamaged;
}

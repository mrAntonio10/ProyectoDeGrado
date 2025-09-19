package com.upb.models.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StereumVerifyChargeRequest {
    private String transactionId;

    //IMPORTANTE - x-api-key
    private String stereumJwt;

}

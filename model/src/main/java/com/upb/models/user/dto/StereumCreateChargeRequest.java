package com.upb.models.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StereumCreateChargeRequest {
    private String country;
    private String amount;
    private String currency; //BOD / USDT
    private String network; // cUURENCI USDT Puede usar POLYGON COMO INFO!
    private String idempotencyKey;
    private String chargeReason;

    private String callbak;

    //Customer body
    private String docNumber;

    //IMPORTANTE - x-api-key
    private String stereumJwt;

}

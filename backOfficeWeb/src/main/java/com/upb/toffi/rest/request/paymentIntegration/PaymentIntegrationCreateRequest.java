package com.upb.toffi.rest.request.paymentIntegration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntegrationCreateRequest {
    private String user;
    private String password;
    private String apiKey;
    private String idEnterprise;
}

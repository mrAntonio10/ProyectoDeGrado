package com.upb.cores;

import org.springframework.stereotype.Service;

@Service
public interface PaymentIntegrationService {
   String createPayment(String user, String password, String apiKey, String idEnterprise);
}

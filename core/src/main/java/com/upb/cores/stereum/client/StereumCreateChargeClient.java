package com.upb.cores.stereum.client;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.paymentsIntegration.PaymentIntegration;
import com.upb.models.user.User;
import com.upb.models.user.dto.StereumCreateChargeRequest;
import com.upb.repositories.BranchOfficeRepository;
import com.upb.repositories.PaymentIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StereumCreateChargeClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PaymentIntegrationRepository paymentIntegrationRepository;

    @Value("${stereum.api.base-url}")
    private String baseUrl;


    public JsonNode createCharge(StereumCreateChargeRequest req, User user) {
        try {
            String idEnterprise = branchOfficeRepository.findEnterpriseIdByUserId(user.getId());
            PaymentIntegration pay = paymentIntegrationRepository.getPaymentIntegrationConfig(idEnterprise)
                    .orElseThrow(() -> new NoSuchElementException("No fue posible recuperar la integracion de pagos"));

            // ===== 1. Construir body =====
            Map<String, Object> body = new HashMap<>();

            StringUtilMod.throwStringIsNullOrEmpty(req.getCountry(), "country");
            StringUtilMod.throwStringIsNullOrEmpty(req.getAmount(), "amount");
            StringUtilMod.throwStringIsNullOrEmpty(req.getCurrency(), "currency");
            StringUtilMod.throwStringIsNullOrEmpty(req.getCountry(), "idempotency_key");



            body.put("country", req.getCountry());
            body.put("amount", req.getAmount());
            body.put("currency", req.getCurrency());
            if (req.getCurrency().equals("USDT")) {
                StringUtilMod.throwStringIsNullOrEmpty(req.getNetwork(), "network");
                body.put("network", req.getNetwork());
            }
            body.put("idempotency_key", req.getIdempotencyKey());
            body.put("charge_reason", StringUtil.isNullOrEmpty(req.getChargeReason()) ? "" : req.getChargeReason() );
            body.put("callback", StringUtil.isNullOrEmpty(req.getCallbak()) ? "" : req.getCallbak());

            // Customer object
            Map<String, Object> customer = new HashMap<>();
            customer.put("name", user.getName());
            customer.put("lastname", user.getLastname());
            if (req.getDocNumber() != null) customer.put("document_number", req.getDocNumber());
            customer.put("email", user.getEmail());
            customer.put("phone", StringUtil.isNullOrEmpty(user.getPhoneNumber()) ? "" : user.getPhoneNumber());

            body.put("customer", customer);

            // ===== 2. Configurar headers =====
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(req.getStereumJwt()); // usa el JWT que viene del controller
            headers.set("x-api-key", pay.getApiKey()); // tomado del properties

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // ===== 3. Ejecutar request =====
            String url = baseUrl + "/transactions/create-charge";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return objectMapper.readTree(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error creando cargo en Stereum", e);
        }
    }
}

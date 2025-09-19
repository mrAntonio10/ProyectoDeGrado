package com.upb.cores.stereum.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upb.models.paymentsIntegration.PaymentIntegration;
import com.upb.models.user.User;
import com.upb.models.user.dto.StereumVerifyChargeRequest;
import com.upb.repositories.BranchOfficeRepository;
import com.upb.repositories.PaymentIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StereumVerifyTransactionClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private final BranchOfficeRepository branchOfficeRepository;
    private final PaymentIntegrationRepository paymentIntegrationRepository;

    @Value("${stereum.api.base-url}")
    private String baseUrl;

//    @Value("${stereum.api.key}")
//    private String apiKey;

    /**
     * Verifica el estado de un cargo de Stereum.
     *
     * @param request Objeto con transactionId y JWT de autenticación
     * @return JsonNode con toda la respuesta de Stereum
     */
    public JsonNode verifyCharge(StereumVerifyChargeRequest request, User user) {
        try {
            String idEnterprise = branchOfficeRepository.findEnterpriseIdByUserId(user.getId());
            PaymentIntegration pay = paymentIntegrationRepository.getPaymentIntegrationConfig(idEnterprise)
                    .orElseThrow(() -> new NoSuchElementException("No fue posible recuperar la integracion de pagos"));


            // ===== 1. Construir URL =====
            if (request.getTransactionId() == null || request.getTransactionId().isEmpty()) {
                throw new IllegalArgumentException("transactionId no puede ser null ni vacío");
            }
            String url = baseUrl + "/transactions/" + request.getTransactionId() + "/verify";

            // ===== 2. Configurar headers =====
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(request.getStereumJwt());
            headers.set("x-api-key", pay.getApiKey());
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            // ===== 3. Ejecutar request =====
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            // ===== 4. Parsear respuesta =====
            return objectMapper.readTree(response.getBody());

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error verificando transacción en Stereum: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error verificando transacción en Stereum", e);
        }
    }
}

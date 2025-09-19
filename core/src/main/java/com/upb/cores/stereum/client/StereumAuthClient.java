package com.upb.cores.stereum.client;

import com.upb.cores.stereum.dto.AuthRequest;
import com.upb.cores.stereum.dto.AuthResponse;
import com.upb.cores.stereum.service.StereumCryptoService;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.paymentsIntegration.PaymentIntegration;
import com.upb.repositories.BranchOfficeRepository;
import com.upb.repositories.PaymentIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StereumAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final StereumCryptoService cryptoService;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PaymentIntegrationRepository paymentIntegrationRepository;

    @Value("${stereum.api.base-url}")
    private String baseUrl;

//    @Value("${stereum.api.username}")
//    private String username;
//
//    @Value("${stereum.api.password}")
//    private String password;

    private String cachedToken;
    private long tokenExpirationEpochMillis = 0;

    public synchronized String getJwtToken(String idUser) throws Exception {
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < tokenExpirationEpochMillis) {
            return cachedToken;
        }

        String idEnterprise = branchOfficeRepository.findEnterpriseIdByUserId(idUser);
        PaymentIntegration pay = paymentIntegrationRepository.getPaymentIntegrationConfig(idEnterprise)
                .orElseThrow(() -> new NoSuchElementException("No fue posible recuperar la integracion de pagos"));

        // 1. Encriptar contraseña
        String encryptedPassword = cryptoService.encryptPassword(pay.getPassword());

        // 2. Crear request body
        AuthRequest body = new AuthRequest(pay.getUser(), encryptedPassword);

        // 3. Configurar headers para forzar JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(body, headers);

        // 4. Realizar request
        String url = baseUrl + "/auth/token";
        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                AuthResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            cachedToken = response.getBody().getAccess_token();
            tokenExpirationEpochMillis = now + (7 * 60 + 50) * 60 * 1000;
            return cachedToken;
        } else {
            throw new IllegalStateException("Error autenticando con Stereum: " + response.getStatusCode());
        }
    }
}

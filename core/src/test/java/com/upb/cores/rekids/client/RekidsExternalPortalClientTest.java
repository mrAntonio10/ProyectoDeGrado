package com.upb.cores.rekids.client;

import com.upb.models.product.dto.RekidsSandboxProductDto;
import com.upb.models.product.dto.RekidsSandboxProductPageResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RekidsExternalPortalClientTest {

    private static final String TOKEN = "rk_test_" + "a".repeat(64);

    private RestTemplate mockRestTemplate;
    private RekidsExternalPortalClient client;

    @BeforeEach
    void setUp() {
        client = new RekidsExternalPortalClient();
        ReflectionTestUtils.setField(client, "configuredToken", TOKEN);
        ReflectionTestUtils.setField(client, "externalPortalUrl", "https://testendpoints.onrender.com");
        mockRestTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", mockRestTemplate);
    }

    @Test
    void returnsProductsFromExternalFeignResponse() {
        String branchId = RekidsExternalPortalClient.BRANCH_LA_PAZ_ID;
        RekidsSandboxProductDto product = RekidsSandboxProductDto.builder()
                .id("20000000-0000-4000-8000-000000000001")
                .branchId(branchId)
                .availability("available")
                .build();
        RekidsSandboxProductPageResponseDto response = RekidsSandboxProductPageResponseDto.builder()
                .items(List.of(product))
                .build();
        when(mockRestTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(RekidsSandboxProductPageResponseDto.class)))
                .thenReturn(ResponseEntity.ok(response));

        RekidsSandboxProductPageResponseDto result = client.listProductsByBranch(branchId, null, null);

        assertSame(product, result.getItems().get(0));
        assertEquals(1, result.getAvailableCount());
        verify(mockRestTemplate).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(RekidsSandboxProductPageResponseDto.class));
    }

    @Test
    void doesNotHideExternalGatewayFailures() {
        IllegalStateException gatewayFailure = new IllegalStateException("gateway unavailable");
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(com.upb.models.product.dto.RekidsSandboxBranchListResponseDto.class)))
                .thenThrow(gatewayFailure);

        assertSame(gatewayFailure, assertThrows(IllegalStateException.class, client::listBranches));
    }

    @Test
    void rejectsMissingTokenWithoutCallingGateway() {
        ReflectionTestUtils.setField(client, "configuredToken", "");

        assertThrows(IllegalStateException.class, client::listBranches);
        verifyNoInteractions(mockRestTemplate);
    }
}

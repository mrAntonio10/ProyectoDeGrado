package com.upb.toffi.rest.request.product;

import com.upb.models.product.dto.ExternalGarmentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncGarmentsRequest {
    private String region;
    private List<ExternalGarmentDto> garments;
}

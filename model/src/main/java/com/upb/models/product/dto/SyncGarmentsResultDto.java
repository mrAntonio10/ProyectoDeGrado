package com.upb.models.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncGarmentsResultDto {
    private int totalFetched;
    private int createdCount;
    private int updatedCount;
    private int addedToNovedadesCount;
    private String syncedByAdmin;
    private Long syncTimestamp;
    private String source;
    private List<ProductWithImageDto> garments;
}

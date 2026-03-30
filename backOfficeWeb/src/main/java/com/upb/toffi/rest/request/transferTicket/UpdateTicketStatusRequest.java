package com.upb.toffi.rest.request.transferTicket;

import com.upb.models.transferTicket.dto.DetailUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    private String newStatus;  // ACCEPTED | REJECTED | PARTIALLY_DAMAGED
    private String comments;
    private List<DetailUpdateDto> details;
}

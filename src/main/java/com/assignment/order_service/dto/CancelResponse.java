package com.assignment.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelResponse {
    private Long productId;
    private int refundAmount;
    private int remainingOrderTotal;
}

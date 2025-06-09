package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelResponse {
    @Schema(description = "상품 아이디")
    private Long productId;

    @Schema(description = "환불 금액")
    private int refundPrice;

    @Schema(description = "취소 후 남은 금액")
    private int remainingPrice;
}

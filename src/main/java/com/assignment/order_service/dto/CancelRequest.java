package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 취소 request
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {
    @Schema(description = "상품 아이디")
    private Long productId;
}

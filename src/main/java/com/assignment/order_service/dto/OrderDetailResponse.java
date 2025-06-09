package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderDetailResponse {

    @Schema(description = "주문 아이디")
    private Long orderId;

    @Schema(description = "주문 생성일자")
    private String createdAt;

    @Schema(description = "주문 상품 목록")
    private List<ItemDetail> items;

    @Schema(description = "주문 전체 금액")
    private int totalPrice;

    @Getter @Setter
    @AllArgsConstructor
    public static class ItemDetail {
        private Long productId;
        private String productName;
        private int quantity;
        private int totalDiscountedPrice;
        private int totalPrice;
        private boolean cancelled;
    }
}

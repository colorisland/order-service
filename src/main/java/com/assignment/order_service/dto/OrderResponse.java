package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "주문 생성 Response")
public class OrderResponse {

    @Schema(description = "주문 아이디", example = "1")
    private Long orderId;

    @Schema(description = "주문 상품 리스트")
    private List<Item> items;

    @Schema(description = "전체 주문 금액")
    private int totalPrice;

    @Getter @Setter
    @AllArgsConstructor
    @Schema(description = "주문 상품")
    public static class Item {

        @Schema(description = "상품 아이디", example = "1")
        private Long productId;

        @Schema(description = "주문 수량")
        private int quantity;

        @Schema(description = "할인 적용된 단가(실 구매금액)", example = "7000")
        private int discountedPrice;

        @Schema(description = "전체 구매 금액", example = "14000")
        private int totalDiscountedPrice;
    }
}

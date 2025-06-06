package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "주문 생성 Request")
public class OrderRequest {
    private List<Item> items;

    @Getter @Setter
    public static class Item {
        @Schema(description = "상품 ID", example = "1000000001")
        private Long productId;

        @Schema(description = "수량", example = "2")
        private int quantity;
    }
}

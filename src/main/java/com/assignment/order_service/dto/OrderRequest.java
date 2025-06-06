package com.assignment.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 생성 Request")
public class OrderRequest {
    private List<Item> items;

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @Schema(description = "상품 ID", example = "1000000001")
        private Long productId;

        @Schema(description = "수량", example = "2")
        private int quantity;
    }
}

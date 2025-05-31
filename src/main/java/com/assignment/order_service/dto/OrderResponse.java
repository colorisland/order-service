package com.assignment.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private List<ItemDetail> items;
    private int totalAmount;

    @Getter @Setter
    @AllArgsConstructor
    public static class ItemDetail {
        private Long productId;
        private int quantity;
        private int discountedPrice;
    }
}

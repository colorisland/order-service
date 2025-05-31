package com.assignment.order_service.service;

import com.assignment.order_service.dto.*;
import jakarta.validation.Valid;

public interface OrderService {
    OrderResponse createOrder(@Valid OrderRequest request);

    CancelResponse cancelOrderItem(Long orderId, @Valid CancelRequest request);

    OrderDetailResponse getOrderDetails(Long orderId);
}

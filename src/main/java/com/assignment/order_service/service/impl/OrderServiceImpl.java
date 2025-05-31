package com.assignment.order_service.service.impl;

import com.assignment.order_service.dto.*;
import com.assignment.order_service.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        return null;
    }

    @Override
    public CancelResponse cancelOrderItem(Long orderId, CancelRequest request) {
        return null;
    }

    @Override
    public OrderDetailResponse getOrderDetails(Long orderId) {
        return null;
    }
}

package com.assignment.order_service.controller;

import com.assignment.order_service.dto.*;
import com.assignment.order_service.exception.SuccessCode;
import com.assignment.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ORDER_CREATED, orderResponse));
    }

    /**
     * 주문 상품 개별 취소
     * @param orderId
     * @param request
     * @return
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<CancelResponse> cancelOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid CancelRequest request) {
        return ResponseEntity.ok(orderService.cancelOrderItem(orderId, request));
    }

    /**
     * 주문 조회
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }
}

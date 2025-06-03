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
     * @param cancelRequest
     * @return
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<CancelResponse>> cancelOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid CancelRequest cancelRequest) {
        CancelResponse cancelResponse = orderService.cancelOrderItem(orderId, cancelRequest);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ORDER_CANCELLED,cancelResponse));
    }

    /**
     * 주문 조회
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetails(@PathVariable Long orderId) {
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ORDER_DETAILS_FETCHED,orderDetailResponse));
    }
}

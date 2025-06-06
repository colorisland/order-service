package com.assignment.order_service.controller;

import com.assignment.order_service.dto.*;
import com.assignment.order_service.exception.SuccessCode;
import com.assignment.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 서비스 API")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     * @param request
     * @return
     */
    @PostMapping
    @Operation(summary = "주문 생성", description = "상품 ID와 수량 리스트를 받아 주문을 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주문 생성 성공",
                            content = @Content(schema = @Schema(implementation = BusinessResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = BusinessResponse.class))
                    )
            })
    public ResponseEntity<BusinessResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        return ResponseEntity.ok(BusinessResponse.success(SuccessCode.ORDER_CREATED, orderResponse));
    }

    /**
     * 주문 상품 개별 취소
     * @param orderId
     * @param cancelRequest
     * @return
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<BusinessResponse<CancelResponse>> cancelOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid CancelRequest cancelRequest) {
        CancelResponse cancelResponse = orderService.cancelOrderItem(orderId, cancelRequest);
        return ResponseEntity.ok(BusinessResponse.success(SuccessCode.ORDER_CANCELLED,cancelResponse));
    }

    /**
     * 주문 조회
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<BusinessResponse<OrderDetailResponse>> getOrderDetails(@PathVariable Long orderId) {
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(BusinessResponse.success(SuccessCode.ORDER_DETAILS_FETCHED,orderDetailResponse));
    }
}

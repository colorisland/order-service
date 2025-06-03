package com.assignment.order_service.service.impl;

import com.assignment.order_service.domain.Order;
import com.assignment.order_service.domain.OrderItem;
import com.assignment.order_service.domain.Product;
import com.assignment.order_service.domain.repository.OrderRepository;
import com.assignment.order_service.domain.repository.ProductRepository;
import com.assignment.order_service.dto.*;
import com.assignment.order_service.exception.BusinessException;
import com.assignment.order_service.exception.ErrorCode;
import com.assignment.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     * @param request
     * @return
     */
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;

        for (OrderRequest.Item item : request.getItems()) {
            // 상품 정보를 가져온다. 존재하지 않는 상품은 예외 처리.
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            int discountedPrice = product.getPrice() - product.getDiscountAmount();
            int totalPrice = discountedPrice * item.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .discountedPrice(discountedPrice)
                    .totalPrice(totalPrice)
                    .isCancelled(false)
                    .build();

            orderItems.add(orderItem);
            totalAmount += totalPrice;
        }

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .orderItems(orderItems)
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);  // 양방향 연관관계 설정
        }

        Order savedOrder = orderRepository.save(order);

        List<OrderResponse.Item> responseItems = orderItems.stream()
                .map(i -> new OrderResponse.Item(
                        i.getProduct().getId(),
                        i.getQuantity(),
                        i.getDiscountedPrice(),
                        i.getTotalPrice()))
                .toList();

        return new OrderResponse(savedOrder.getId(), responseItems, totalAmount);
    }

    /**
     * 주문 상품 개별 취소
     * @param orderId
     * @param cancelRequest
     * @return
     */
    @Override
    @Transactional
    public CancelResponse cancelOrderItem(Long orderId, CancelRequest cancelRequest) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 항목 조회
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(i -> i.getId().equals(cancelRequest.getProductId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 이미 취소된 경우
        if (orderItem.isCancelled()) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED);
        }

        // 환불 처리
        orderItem.setCancelled(true);

        // 재고 원복
        Product product = orderItem.getProduct();
        product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());

        // 총 주문 금액에서 환불 금액 차감
        int refundAmount = orderItem.getTotalPrice();
        int remainingTotal = order.getOrderItems().stream()
                .filter(i -> !i.isCancelled())
                .mapToInt(OrderItem::getTotalPrice)
                .sum();

        return new CancelResponse(orderItem.getId(), refundAmount, remainingTotal);
    }

    @Override
    public OrderDetailResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderDetailResponse.ItemDetail> items = order.getOrderItems().stream()
                .map(i -> new OrderDetailResponse.ItemDetail(
                        i.getProduct().getId(),
                        i.getQuantity(),
                        i.getDiscountedPrice(),
                        i.isCancelled()
                ))
                .toList();

        int totalAmount = order.getOrderItems().stream()
                .filter(i -> !i.isCancelled())
                .mapToInt(OrderItem::getTotalPrice)
                .sum();

        return new OrderDetailResponse(orderId,items, totalAmount);
    }
}

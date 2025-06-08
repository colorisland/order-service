package com.assignment.order_service.service.impl;

import com.assignment.order_service.domain.Order;
import com.assignment.order_service.domain.OrderItem;
import com.assignment.order_service.domain.Product;
import com.assignment.order_service.domain.repository.OrderRepository;
import com.assignment.order_service.domain.repository.ProductRepository;
import com.assignment.order_service.dto.*;
import com.assignment.order_service.exception.BusinessException;
import com.assignment.order_service.enums.ErrorCode;
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

        // 전체 주문 금액.
        int orderTotalPrice = 0;

        // 요청 상품이 없으면 예외
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_ORDER_ITEMS);
        }

        // 요청 상품 리스트 순회하면서 orderItem 객체 생성.
        for (OrderRequest.Item item : request.getItems()) {
            // 상품 정보를 가져온다. 존재하지 않는 상품은 예외 처리.
            Product product = productRepository.findByIdForUpdate(item.getProductId())
                    .orElseThrow(()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 재고 부족 예외 처리
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            // 할인가 계산
            int discountedPrice = product.getPrice() - product.getDiscountAmount();

            // 전체 할인가 계산
            int totalDiscountedPrice = discountedPrice * item.getQuantity();

            // 해당 상품의 할인적용 안된 판매가격으로 계산.
            int totalPrice = product.getPrice() * item.getQuantity();

            // 주문 아이템 생성.
            OrderItem orderItem = OrderItem.builder()
                    .product(product) // 상품 관계 설정.
                    .quantity(item.getQuantity())
                    .totalDiscountedPrice(totalDiscountedPrice)
                    .totalPrice(totalPrice)
                    .isCancelled(false)
                    .build();

            // orderItems 에 추가.
            orderItems.add(orderItem);
            orderTotalPrice += totalPrice;
        }

        // 주문 객체 설정.
        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .orderItems(orderItems)
                .build();

        // 주문 아이템에 주문 설정.
        for (OrderItem item : orderItems) {
            item.setOrder(order);  // 양방향 연관관계 설정
        }

        // 주문, 주문아이템 생성하고 결과 반환.
        Order savedOrder = orderRepository.save(order);

        // Response DTO 설정
        List<OrderResponse.Item> responseItems = orderItems.stream()
                .map(i -> new OrderResponse.Item(
                        i.getProduct().getId(),
                        i.getQuantity(),
                        i.getTotalDiscountedPrice(),
                        i.getTotalPrice()))
                .toList();

        return new OrderResponse(savedOrder.getId(), responseItems, orderTotalPrice);
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
                .filter(i -> i.getProduct().getId().equals(cancelRequest.getProductId()))
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
    @Transactional
    public OrderDetailResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderDetailResponse.ItemDetail> items = order.getOrderItems().stream()
                .map(i -> new OrderDetailResponse.ItemDetail(
                        i.getProduct().getId(),
                        i.getQuantity(),
                        i.getTotalDiscountedPrice(),
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

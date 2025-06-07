package com.assignment.order_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 아이템")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "주문 아이템 아이디")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    @Schema(description = "주문과의 관계")
    private Order order;

    // 상품과의 관계
    @ManyToOne
    @JoinColumn(name = "product_id")
    @Schema(description = "상품과의 관계")
    private Product product;

    @Schema(description = "수량")
    private int quantity;
    
    @Column(name = "total_discounted_price")
    @Schema(description = "할인가격")
    private int totalDiscountedPrice;

    @Column(name = "total_price")
    @Schema(description = "전체 가격")
    private int totalPrice;

    @Column(name = "is_cancelled")
    @Schema(description = "취소 여부")
    private boolean isCancelled = false;
}
package com.assignment.order_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문과의 관계
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // 상품과의 관계
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Column(name = "discounted_price")
    private int discountedPrice;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "is_cancelled")
    private boolean isCancelled = false;
}
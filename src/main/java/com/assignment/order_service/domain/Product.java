package com.assignment.order_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Schema(description = "상품 아이디")
    private Long id;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "원가")
    private int price;

    @Column(name = "discount_amount")
    @Schema(description = "할인 양")
    private int discountAmount;

    @Column(name = "stock_quantity")
    @Schema(description = "재고")
    private int stockQuantity;
}

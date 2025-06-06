package com.assignment.order_service;

import com.assignment.order_service.domain.repository.ProductRepository;
import com.assignment.order_service.dto.OrderRequest;
import com.assignment.order_service.dto.OrderResponse;
import com.assignment.order_service.service.OrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 테스트 메서드 이름: 주어진 상황_결과 조합으로 만들기.
 */
@SpringBootTest
@DisplayName("OrderService 테스트")
public class OrderServiceApplicationTests {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductRepository productRepository;

	@Test
	@DisplayName("단일 상품 정상 주문 성공")
	void createSingleItemOrder_success() {
		// given
		// 이마트 생수 2개 주문.
		Long productId = 1000000001L;
		int quantity = 2;

		// 주문 요청 생성.
		OrderRequest.Item item = new OrderRequest.Item(productId, quantity);
		OrderRequest request = new OrderRequest(List.of(item));

		// when
		// 주문 생성.
		OrderResponse response = orderService.createOrder(request);

		// then
		// 주문 결과는 null 이 아니어야함.
		assertThat(response).isNotNull();
		//
		assertThat(response.getItems()).hasSize(1);
		assertThat(response.getTotalPrice()).isEqualTo((800 - 100) * quantity);

		OrderResponse.Item orderItem = response.getItems().get(0);
		assertThat(orderItem.getProductId()).isEqualTo(productId);
		assertThat(orderItem.getQuantity()).isEqualTo(quantity);
		assertThat(orderItem.getDiscountedPrice()).isEqualTo(700);
	}

	@Test
	@DisplayName("여러 상품 정상 주문")
	void createMultipleItemOrder_success() {
		// given
		OrderRequest request = new OrderRequest(List.of(
				new OrderRequest.Item(1000000002L, 2), // 신라면 멀티팩
				new OrderRequest.Item(1000000003L, 3)  // 바나나 한 송이
		));

		// when
		OrderResponse response = orderService.createOrder(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getItems()).hasSize(2);
		assertThat(response.getTotalPrice()).isEqualTo(
				(4200 - 500) * 2 + (3500 - 300) * 3 //  7400 + 9600 = 17000
		);
	}

}

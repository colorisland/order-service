package com.assignment.order_service;

import com.assignment.order_service.domain.repository.ProductRepository;
import com.assignment.order_service.dto.OrderRequest;
import com.assignment.order_service.dto.OrderResponse;
import com.assignment.order_service.service.OrderService;
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
		// 주문 결과의 상품 리스트 1개.
		assertThat(response.getItems()).hasSize(1);
		// 전체가격 계산.
		assertThat(response.getTotalPrice()).isEqualTo((800) * quantity);

		// 상품 인스턴스 확인.
		OrderResponse.Item orderItem = response.getItems().get(0);
		// 주문한 상품 번호 확인.
		assertThat(orderItem.getProductId()).isEqualTo(productId);
		// 수량 확인.
		assertThat(orderItem.getQuantity()).isEqualTo(quantity);
		// 상품의 실구매 가격 확인.
		assertThat(orderItem.getTotalDiscountedPrice()).isEqualTo((700) * quantity);
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
		// 주문 생성응답이 null 이 아니다.
		assertThat(response).isNotNull();
		// 주문상품 리스트 크기 == 2.
		assertThat(response.getItems()).hasSize(2);
		// 전체 주문 금액 = 주문상품 실구매가 + 총 할인가격
		assertThat(response.getTotalPrice()).isEqualTo(
				response.getItems().stream().mapToInt(OrderResponse.Item::getTotalPrice).sum());
	}

}

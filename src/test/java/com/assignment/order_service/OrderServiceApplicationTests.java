package com.assignment.order_service;

import com.assignment.order_service.domain.repository.ProductRepository;
import com.assignment.order_service.dto.*;
import com.assignment.order_service.exception.BusinessException;
import com.assignment.order_service.enums.ErrorCode;
import com.assignment.order_service.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

	// ******************************************** 주문 생성 ***************************************************

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

	@Test
	@DisplayName("존재하지 않는 상품 ID로 주문 요청 시 PRODUCT_NOT_FOUND 예외 발생")
	void createOrderWithInvalidProductId_productNotFound() {
		// given
		long invalidProductId = 9999999999L;
		OrderRequest.Item item = new OrderRequest.Item(invalidProductId, 1);
		OrderRequest request = new OrderRequest(List.of(item));

		// when
		Throwable thrown = catchThrowable(() -> orderService.createOrder(request));

		// then
		assertThat(thrown)
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
	}

	@Test
	@DisplayName("주문생성 동시성 테스트")
	void createOrder_concurrentRequest() throws InterruptedException {
		// given - 초기 재고 300
		Long productId = 1000000005L; // 오리온 초코파이
		int orderQuantity = 300;

		OrderRequest.Item item = new OrderRequest.Item(productId, orderQuantity);
		OrderRequest request = new OrderRequest(List.of(item));

		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		List<Future<String>> results = new ArrayList<>();

		// when
		for (int i = 0; i < threadCount; i++) {
			Future<String> future = executorService.submit(() -> {
				try {
					orderService.createOrder(request);
					return "SUCCESS";
				} catch (BusinessException e) {
					return e.getErrorCode().name();
				} finally {
					latch.countDown();
				}
			});
			results.add(future);
		}

		latch.await();

		// then
		long successCount = results.stream().filter(f -> {
			try {
				return f.get().equals("SUCCESS");
			} catch (Exception e) {
				return false;
			}
		}).count();

		long failCount = results.stream().filter(f -> {
			try {
				return f.get().equals("INSUFFICIENT_STOCK");
			} catch (Exception e) {
				return false;
			}
		}).count();

		assertThat(successCount).isEqualTo(1);
		assertThat(failCount).isEqualTo(1);
	}

	@Test
	@DisplayName("재고 수량보다 많은 수량 요청 - INSUFFICIENT_STOCK 예외")
	void createOrder_insufficientStock_exception() {
		// given
		Long productId = 1000000005L; // 오리온 초코파이, 재고 300이지만 99999개 요청.
		int requestQuantity = 99999;

		OrderRequest.Item item = new OrderRequest.Item(productId, requestQuantity);
		OrderRequest request = new OrderRequest(List.of(item));

		// when, then
		// 주문 생성 후 재고 부족 예외 처리되는지 확인.
		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.INSUFFICIENT_STOCK.getMessage());
	}

	@Test
	@DisplayName("비어있는 주문 상품 요청 - EMPTY_ORDER_ITEMS 예외")
	void createOrder_withEmptyItems_EmptyOrderItems_exception() {
		// given
		OrderRequest request = new OrderRequest(Collections.emptyList());

		// when , then 주문상품 예외처리 확인.
		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.EMPTY_ORDER_ITEMS.getMessage());
	}

	// ******************************************** 주문 상품 개별 취소 ***************************************************
	@Test
	@DisplayName("주문 상품 개별 취소 - 정상 케이스")
	void cancelOrderItem_success() {
		// given
		OrderRequest request = new OrderRequest(List.of(
				new OrderRequest.Item(1000000001L, 3)
		));
		OrderResponse orderResponse = orderService.createOrder(request);

		Long orderId = orderResponse.getOrderId();
		Long productId = orderResponse.getItems().get(0).getProductId();

		CancelRequest cancelRequest = new CancelRequest(productId);

		// when
		CancelResponse cancelResponse = orderService.cancelOrderItem(orderId, cancelRequest);

		// then
		assertThat(cancelResponse.getRefundPrice()).isGreaterThan(0);
		assertThat(cancelResponse.getRemainingPrice()).isEqualTo(0);
	}

	@Test
	@DisplayName("존재하지 않는 주문 ID로 취소 요청 시 ORDER_NOT_FOUND 예외 발생")
	void cancelOrder_withInvalidOrderId_orderNotFound_exception() {
		// given
		Long invalidOrderId = 99999L; // 존재하지 않는 주문 ID
		Long validProductId = 1000000001L; // 실제 존재하는 상품 ID

		CancelRequest request = new CancelRequest(validProductId);

		// when & then
		assertThatThrownBy(() -> orderService.cancelOrderItem(invalidOrderId, request))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("존재하는 주문 ID지만 해당 상품 ID가 없을 경우 PRODUCT_NOT_FOUND 예외 발생")
	void cancelOrder_withInvalidProductIdInOrder_productNotFound_exception() {
		// given
		Long validProductId = 1000000001L; // 이마트 생수 사기.
		int quantity = 1;

		// 주문 먼저 생성
		OrderRequest.Item item = new OrderRequest.Item(validProductId, quantity);
		OrderRequest orderRequest = new OrderRequest(List.of(item));
		OrderResponse orderResponse = orderService.createOrder(orderRequest);

		Long orderId = orderResponse.getOrderId();
		Long invalidProductId = 9999999999L; // 주문에 포함되지 않은 상품 ID

		// 주문상품 개별 취소 생성.
		CancelRequest cancelRequest = new CancelRequest(invalidProductId);

		// when & then
		assertThatThrownBy(() -> orderService.cancelOrderItem(orderId, cancelRequest))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("이미 취소된 상품 재취소 시 ALREADY_CANCELLED 예외 발생")
	void cancelOrder_twice_alreadyCancelled_exception() {
		// given
		Long productId = 1000000001L;
		int quantity = 1;

		// 주문 먼저 생성
		OrderRequest orderRequest = new OrderRequest(List.of(new OrderRequest.Item(productId, quantity)));
		OrderResponse orderResponse = orderService.createOrder(orderRequest);
		Long orderId = orderResponse.getOrderId();

		CancelRequest cancelRequest = new CancelRequest(productId);

		// 1차 취소 성공
		CancelResponse cancelResponse = orderService.cancelOrderItem(orderId, cancelRequest);
		assertThat(cancelResponse).isNotNull();
		assertThat(cancelResponse.getRefundPrice()).isGreaterThan(0);

		// 2차 중복 취소
		assertThatThrownBy(() -> orderService.cancelOrderItem(orderId, cancelRequest))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.ALREADY_CANCELLED.getMessage());
	}

	// ******************************************** 주문 상품 조회 ***************************************************

	@Test
	@DisplayName("주문 상세 조회 - 정상 조회")
	void getOrderDetails_success() {
		// given
		// 이마트 생수 2개, 신라면 멀티팩 1개 주문.
		OrderRequest request = new OrderRequest(List.of(
				new OrderRequest.Item(1000000001L, 2),
				new OrderRequest.Item(1000000002L, 1)
		));
		// 주문 생성
		OrderResponse createdOrder = orderService.createOrder(request);
		Long orderId = createdOrder.getOrderId();

		// when
		// 생성된 주문 아이디로 상세내용 받아오기.
		OrderDetailResponse detailResponse = orderService.getOrderDetails(orderId);

		// then
		// 주문 아이디, 상품 리스트 같은지 확인.
		assertThat(detailResponse.getOrderId()).isEqualTo(orderId);
		assertThat(detailResponse.getItems()).hasSize(2);

		assertThat(detailResponse.getItems())
				.anySatisfy(i -> {
					assertThat(i.getProductId()).isEqualTo(1000000001L);
					assertThat(i.getQuantity()).isEqualTo(2);
				})
				.anySatisfy(i -> {
					assertThat(i.getProductId()).isEqualTo(1000000002L);
					assertThat(i.getQuantity()).isEqualTo(1);
				});

		assertThat(detailResponse.getTotalPrice()).isGreaterThan(0);
	}

	@Test
	@DisplayName("주문 상세 조회 실패 - 존재하지 않는 주문 ID")
	void getOrderDetails_orderNotFound_exception() {
		// given
		Long invalidOrderId = 999999L;

		// when & then
		// 주문 조회 실패 예외처리.
		assertThatThrownBy(() -> orderService.getOrderDetails(invalidOrderId))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
	}
}

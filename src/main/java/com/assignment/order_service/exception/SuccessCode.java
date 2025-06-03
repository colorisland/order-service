package com.assignment.order_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    ORDER_CREATED("주문이 성공적으로 생성되었습니다.", HttpStatus.CREATED),
    ORDER_CANCELLED("주문이 성공적으로 취소되었습니다.", HttpStatus.OK),
    ORDER_DETAILS_FETCHED("주문 상세 조회가 완료되었습니다.", HttpStatus.OK),
    PRODUCT_STOCK_UPDATED("상품 재고가 성공적으로 업데이트되었습니다.", HttpStatus.OK);

    private final String message;
    private final HttpStatus httpStatus;

    SuccessCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

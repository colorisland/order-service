package com.assignment.order_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND("상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    EMPTY_ORDER_ITEMS("주문 요청 상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_CANCELLED("이미 취소된 상품입니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("주문이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

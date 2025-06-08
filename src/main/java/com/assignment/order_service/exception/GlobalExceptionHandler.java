package com.assignment.order_service.exception;

import com.assignment.order_service.dto.BusinessResponse;
import com.assignment.order_service.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BusinessResponse.fail(e.getErrorCode()));
    }
}

package com.assignment.order_service.service.impl;

import com.assignment.order_service.domain.OrderFailureLog;
import com.assignment.order_service.domain.repository.OrderFailureLogRepository;
import com.assignment.order_service.dto.OrderRequest;
import com.assignment.order_service.enums.ErrorCode;
import com.assignment.order_service.service.OrderFailureLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFailureLogServiceImpl implements OrderFailureLogService {
    private final OrderFailureLogRepository failureLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertOrderFailureLog(OrderRequest request, ErrorCode errorCode, String message) {
        String productIds = request.getItems().stream()
                .map(i -> i.getProductId().toString())
                .collect(Collectors.joining(","));

        String quantities = request.getItems().stream()
                .map(i -> String.valueOf(i.getQuantity()))
                .collect(Collectors.joining(","));

        OrderFailureLog log = OrderFailureLog.builder()
                .productIds(productIds)
                .quantities(quantities)
                .errorCode(errorCode.name())
                .errorMessage(message)
                .createdAt(LocalDateTime.now())
                .build();

        failureLogRepository.save(log);
    }
}

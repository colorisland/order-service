package com.assignment.order_service.service;

import com.assignment.order_service.dto.OrderRequest;
import com.assignment.order_service.enums.ErrorCode;

public interface OrderFailureLogService {

    public void insertOrderFailureLog(OrderRequest request, ErrorCode errorCode, String message);
    }

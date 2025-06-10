package com.assignment.order_service.domain.repository;

import com.assignment.order_service.domain.OrderFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFailureLogRepository extends JpaRepository<OrderFailureLog, Long> {
}

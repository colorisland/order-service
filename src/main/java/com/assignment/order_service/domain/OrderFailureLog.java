package com.assignment.order_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_failure_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFailureLog {
    @Id
    @GeneratedValue
    private Long id;

    private String productIds;
    private String quantities;
    private String errorCode;
    private String errorMessage;

    private LocalDateTime createdAt;
}
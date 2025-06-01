package com.assignment.order_service.domain.repository;

import com.assignment.order_service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}

package com.assignment.order_service.domain.repository;

import com.assignment.order_service.domain.Product;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    /**
     * 비관적 락 적용.
     * @param id
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE) // FOR UPDATE 쿼리
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    @QueryHints({ @QueryHint(name = "javax.persistence.lock.timeout", value ="3000") }) // TTL 3초
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}

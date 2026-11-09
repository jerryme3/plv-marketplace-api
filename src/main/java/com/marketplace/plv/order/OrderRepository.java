package com.marketplace.plv.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findAllByBuyerIdOrderByCreatedAtDesc(Long buyerId);

	List<Order> findAllBySellerIdOrderByCreatedAtDesc(Long sellerId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT o FROM Order o WHERE o.id = :orderId")
	java.util.Optional<Order> findByIdForUpdate(@Param("orderId") Long orderId);
}

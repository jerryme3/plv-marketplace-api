package com.marketplace.plv.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findAllByOrderIdOrderById(Long orderId);

	List<OrderItem> findAllByOrderIdInOrderById(Collection<Long> orderIds);
}

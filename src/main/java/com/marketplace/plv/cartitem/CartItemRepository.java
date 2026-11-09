package com.marketplace.plv.cartitem;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	@Query("""
			SELECT ci FROM CartItem ci
			JOIN FETCH ci.product p
			JOIN FETCH p.store s
			JOIN FETCH s.seller
			WHERE ci.cart.id = :cartId
			ORDER BY ci.id
			""")
	List<CartItem> findAllByCartIdWithProductAndSeller(@Param("cartId") Long cartId);

	@Transactional
	void deleteAllByCartId(Long cartId);
}

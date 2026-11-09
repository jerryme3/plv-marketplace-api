package com.marketplace.plv.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.marketplace.plv.buyer.Buyer;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByBuyerId(Long buyerId);

}

package com.marketplace.plv.buyer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    Optional<Buyer> findByUserId(Long id);

    @Query("SELECT b FROM Buyer b JOIN FETCH b.user u WHERE u.account.id = :accountId")
    Optional<Buyer> findByAccountId(@Param("accountId") Long accountId);
}

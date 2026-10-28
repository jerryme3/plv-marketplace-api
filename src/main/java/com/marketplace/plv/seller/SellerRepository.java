package com.marketplace.plv.seller;

import com.marketplace.plv.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

	boolean existsByUser(User user);

	boolean existsByUserId(Long userId);
	Optional<Seller> findByUser(User user);

	@Query("SELECT s FROM Seller s JOIN FETCH s.user u WHERE u.id = :id")
	Optional<Seller> findByUserId(@Param("id") Long userId);

}

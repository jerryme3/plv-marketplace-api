package com.marketplace.plv.store;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	Optional<Store> findByName(String name);

	@Query("SELECT COUNT(p) FROM Product p WHERE p.store.id = :storeId")
	long countProducts(@Param("storeId") Long storeId);
}

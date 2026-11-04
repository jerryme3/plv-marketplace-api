package com.marketplace.plv.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("""
			SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
			FROM Product p
			WHERE p.title = :title AND p.store.id = :storeId
			""")
	boolean existsByTitleAndStore(
			@Param("title") String title,
			@Param("storeId") Long storeId
	);

	@Query("""
			SELECT p FROM Product p
			WHERE (:search IS NULL OR
			       LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
			       LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))
			  AND (:categoryId IS NULL OR p.category.id = :categoryId)
			  AND (:storeId IS NULL OR p.store.id = :storeId)
			""")
	Page<Product> browse(
			@Param("search") String search,
			@Param("categoryId") Long categoryId,
			@Param("storeId") Long storeId,
			Pageable pageable
	);
}

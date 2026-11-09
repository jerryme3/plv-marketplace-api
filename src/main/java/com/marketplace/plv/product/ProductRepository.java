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
			WHERE (:searchPattern IS NULL OR
			       p.title ILIKE :searchPattern OR
			       COALESCE(p.description, '') ILIKE :searchPattern)
			  AND (:categoryId IS NULL OR p.category.id = :categoryId)
			  AND (:storeId IS NULL OR p.store.id = :storeId)
			""")
	Page<Product> browse(
			@Param("searchPattern") String searchPattern,
			@Param("categoryId") Long categoryId,
			@Param("storeId") Long storeId,
			Pageable pageable
	);
}

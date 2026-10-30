package com.marketplace.plv.product_stocks;

import com.marketplace.plv.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStocksRepository extends JpaRepository<ProductStocks, Long> {

	@Query("SELECT p FROM ProductStocks p WHERE p.product = :product")
	int getCurrentStocksForProduct(@Param("product") Product product);

	@Query("""
			UPDATE ProductStocks p
			SET p.stockQuantity = p.stockQuantity - :orderQuantity
			WHERE p.product.id = :productId
			AND p.stockQuantity >= :orderQuantity""")
	@Modifying
	@Transactional
	int decrementStock(@Param("orderQuantity") Integer orderQuantity, @Param("productId") Long productId);

}

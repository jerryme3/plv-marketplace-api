package com.marketplace.plv.product_stocks;

import com.marketplace.plv.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProductStocksRepository extends JpaRepository<ProductStocks, Long> {

	@Query("SELECT ps FROM ProductStocks ps WHERE ps.product.id IN :productIds")
	List<ProductStocks> findAllByProductIds(@Param("productIds") Collection<Long> productIds);

	@Query("SELECT COALESCE(SUM(p.stockQuantity), 0) FROM ProductStocks p WHERE p.product = :product")
	long getCurrentStocksForProduct(@Param("product") Product product);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ps FROM ProductStocks ps WHERE ps.product.id = :productId ORDER BY ps.id")
	List<ProductStocks> findAllByProductIdForUpdate(@Param("productId") Long productId);

}

package com.marketplace.plv.product_stocks;

import com.marketplace.plv.product.Product;
import com.marketplace.plv.shared.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(
		name = "product_stocks",
		check = @CheckConstraint(name = "has_stock", constraint = "total_stocks >= 0")
)
@AttributeOverride(name = "id", column = @Column(name = "product_stock_id"))
@Entity
public class ProductStocks extends AuditedEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "total_stocks", nullable = false)
	private Integer stockQuantity;

}

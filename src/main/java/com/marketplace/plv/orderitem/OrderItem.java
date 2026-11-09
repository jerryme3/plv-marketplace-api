package com.marketplace.plv.orderitem;

import com.marketplace.plv.order.Order;
import com.marketplace.plv.product.Product;
import com.marketplace.plv.shared.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items", check = @CheckConstraint(
		name = "order_item_valid",
		constraint = "quantity > 0 AND unit_price >= 0"))
@AttributeOverride(name = "id", column = @Column(name = "order_item_id"))
public class OrderItem extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false, updatable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false)
	private Product product;

	@Column(name = "product_title", nullable = false, length = 100, updatable = false)
	private String productTitle;

	@Column(name = "quantity", nullable = false, updatable = false)
	private int quantity;

	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2, updatable = false)
	private BigDecimal unitPrice;

}

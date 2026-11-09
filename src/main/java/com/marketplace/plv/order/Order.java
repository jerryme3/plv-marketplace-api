package com.marketplace.plv.order;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.shared.entity.AuditedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "orders", check = @CheckConstraint(
		name = "order_valid",
		constraint = "total_amount >= 0 AND status IN ('PENDING', 'PAID', 'CONFIRMED', 'PACKED', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'REFUNDED')"))
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
public class Order extends AuditedEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "buyer_id", nullable = false, updatable = false)
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", updatable = false)
	private Seller seller;

	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2, updatable = false)
	private BigDecimal totalAmount;

	@Column(name = "delivery_location", length = 100, updatable = false)
	private String deliveryLocation;

	@Column(name = "current_location", length = 100)
	private String currentLocation;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private OrderStatus status = OrderStatus.PENDING;

	public void changeStatus(OrderStatus status) {
		this.status = status;
	}

	public void updateCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

}

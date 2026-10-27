package com.marketplace.plv.cart;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.shared.AuditedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
@AttributeOverride(name = "id", column = @Column(name = "cart_id"))
public class Cart extends AuditedEntity {

	@OneToOne(optional = false)
	@JoinColumn(name = "buyer_id", nullable = false, unique = true)
	private Buyer buyer;

}

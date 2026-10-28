package com.marketplace.plv.seller;

import com.marketplace.plv.shared.entity.BaseEntity;
import com.marketplace.plv.user.User;
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

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sellers")
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
public class Seller extends BaseEntity {

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Builder.Default
	@Column(name = "rating", precision = 3, scale = 2)
	private BigDecimal rating = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "is_verified", nullable = false)
	private boolean verified = false;

}

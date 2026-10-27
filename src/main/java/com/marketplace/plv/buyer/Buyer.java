package com.marketplace.plv.buyer;

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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buyers")
@AttributeOverride(name = "id", column = @Column(name = "buyer_id"))
public class Buyer extends BaseEntity {

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "delivery_building", length = 100)
	private String deliveryBuilding;

}

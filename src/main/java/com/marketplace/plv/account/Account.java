package com.marketplace.plv.account;

import com.marketplace.plv.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@AttributeOverride(name = "id", column = @Column(name = "account_id"))
public class Account extends BaseEntity {

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "hashed_password", nullable = false, length = 150)
	private String password;

}

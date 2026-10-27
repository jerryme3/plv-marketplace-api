package com.marketplace.plv.user;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.shared.AuditedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends AuditedEntity {

	@OneToOne(optional = false)
	@JoinColumn(name = "account_id", nullable = false, unique = true)
	private Account account;

	@Column(name = "username", nullable = false, unique = true, length = 50)
	private String username;

	@Builder.Default
	@Setter
	@Column(name = "is_active", nullable = false)
	private boolean active = false;

	@Column(name = "last_login")
	@Setter
	private Instant lastLogin;

}

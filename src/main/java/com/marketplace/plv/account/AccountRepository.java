package com.marketplace.plv.account;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByEmail(String email);

	boolean existsByEmail(String email);

	@Transactional
	@Modifying
	@Query("UPDATE Account a SET a.role = 'ROLE_ADMIN' WHERE a.id = :accountId")
	void promoteToAdmin(@Param("accountId") Long id);

	@Transactional
	@Modifying
	@Query("UPDATE Account a SET a.role = 'ROLE_USER' WHERE a.id = :accountId")
	void demoteToUser(@Param("accountId") Long id);

}

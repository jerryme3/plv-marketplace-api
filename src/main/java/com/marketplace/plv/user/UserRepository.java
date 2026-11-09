package com.marketplace.plv.user;

import com.marketplace.plv.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	Optional<User> findByAccount(Account account);

	@Query("SELECT u FROM User u WHERE u.account.id = :accountId")
	Optional<User> findByAccountId(@Param("accountId") Long accountId);

	Optional<User> findByUsername(String username);
}

package com.marketplace.plv.user;

import com.marketplace.plv.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	Optional<User> findByAccount(Account account);

	Optional<User> findByUsername(String username);
}

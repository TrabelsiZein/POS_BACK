package com.digithink.business_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByUsername(String username);

}

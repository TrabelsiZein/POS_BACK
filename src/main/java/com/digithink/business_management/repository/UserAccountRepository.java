package com.digithink.business_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.digithink.business_management.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {

	Optional<UserAccount> findByUsername(String username);

	@Query("select new com.digithink.business_management.model.UserAccount(ua.id,ua.createdAt,"
			+ "ua.updatedAt,ua.createdBy,ua.updatedBy,ua.username,ua.email,ua.active) from UserAccount ua")
	List<UserAccount> findAll();

}

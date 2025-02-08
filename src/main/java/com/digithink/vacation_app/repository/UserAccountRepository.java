package com.digithink.vacation_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.digithink.vacation_app.model.UserAccount;

public interface UserAccountRepository extends _BaseRepository<UserAccount, Long> {

	Optional<UserAccount> findByUsername(String username);

	@Query("select new com.digithink.base_app.model.UserAccount(ua.id,ua.createdAt,"
			+ "ua.updatedAt,ua.createdBy,ua.updatedBy,ua.username,ua.fullName,ua.email,ua.active) from UserAccount ua")
	List<UserAccount> findAll();

}

package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.UserAccount;

public interface UserAccountRepository extends _BaseRepository<UserAccount, Long> {

	Optional<UserAccount> findByUsername(String username);

}

package com.digithink.business_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.dto.UserAccountDTO;
import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.repository.UserAccountRepository;

public class UserAccountService extends _BaseService<UserAccount, Long> {

	@Autowired
	private UserAccountRepository accountRepository;

	@Override
	protected JpaRepository<UserAccount, Long> getRepository() {
		return accountRepository;
	}

	public List<UserAccountDTO> getAllUsers() {
		List<UserAccount> users = accountRepository.findAll();
		return users.stream()
				.map(user -> new UserAccountDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
				.collect(Collectors.toList());
	}
}

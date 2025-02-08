package com.digithink.vacation_app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.dto.UserAccountDTO;
import com.digithink.vacation_app.model.UserAccount;
import com.digithink.vacation_app.repository.UserAccountRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class UserAccountService extends _BaseService<UserAccount, Long> {

	@Autowired
	private UserAccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected _BaseRepository<UserAccount, Long> getRepository() {
		return accountRepository;
	}

	public List<UserAccountDTO> getAllUsers() {
		List<UserAccount> users = accountRepository.findAll();
		return users.stream()
				.map(user -> new UserAccountDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
				.collect(Collectors.toList());
	}

	@Override
	public UserAccount save(UserAccount entity) {
		String username = currentUserProvider.getCurrentUserName();

		if (entity.getId() == null) {
			entity.setCreatedBy(username);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		} else {
			entity.setUpdatedBy(username);
			entity.setUpdatedAt(LocalDateTime.now());
		}

		return getRepository().save(entity);
	}
}

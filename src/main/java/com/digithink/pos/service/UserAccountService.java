package com.digithink.pos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.dto.CreateUserRequestDTO;
import com.digithink.pos.dto.UserAccountDTO;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.repository.UserAccountRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserAccountService extends _BaseService<UserAccount, Long> {

	@Autowired
	private UserAccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected _BaseRepository<UserAccount, Long> getRepository() {
		return accountRepository;
	}

	/**
	 * Get all users
	 */
	public List<UserAccountDTO> getAllUsers() {
		List<UserAccount> users = accountRepository.findAll();
		return users.stream()
				.map(user -> UserAccountDTO.fromEntity(user))
				.collect(Collectors.toList());
	}

	/**
	 * Get user by ID
	 */
	public UserAccountDTO getUserById(Long userId) {
		UserAccount user = findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		return UserAccountDTO.fromEntity(user);
	}

	/**
	 * Find user by username
	 */
	public Optional<UserAccount> findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	/**
	 * Find user by badge code
	 */
	public Optional<UserAccount> findByBadgeCode(String badgeCode) {
		return accountRepository.findByBadgeCode(badgeCode);
	}

	/**
	 * Create a new user with role
	 */
	@Transactional
	public UserAccountDTO createUser(CreateUserRequestDTO request) throws Exception {
		// Check if username already exists
		if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new RuntimeException("Username '" + request.getUsername() + "' already exists");
		}

		// Create new user
		UserAccount user = new UserAccount();
		user.setUsername(request.getUsername());
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole(request.getRole());
		user.setActive(true);

		// Save user (password will be encrypted in save method)
		UserAccount savedUser = save(user);
		
		return UserAccountDTO.fromEntity(savedUser);
	}

	/**
	 * Update user role
	 */
	@Transactional
	public UserAccountDTO updateUserRole(Long userId, com.digithink.pos.model.enumeration.Role role) throws Exception {
		UserAccount user = findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		user.setRole(role);
		UserAccount savedUser = save(user);
		
		return UserAccountDTO.fromEntity(savedUser);
	}

	/**
	 * Toggle user active status
	 */
	@Transactional
	public UserAccountDTO toggleUserStatus(Long userId) throws Exception {
		UserAccount user = findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		
		user.setActive(!user.getActive());
		UserAccount savedUser = save(user);
		
		return UserAccountDTO.fromEntity(savedUser);
	}

	@Override
	public UserAccount save(UserAccount entity) throws Exception {
		try {
			String username = currentUserProvider.getCurrentUserName();

			if (entity.getId() == null) {
				entity.setCreatedBy(username);
				entity.setCreatedAt(LocalDateTime.now());
				// Only encode password if it's not already encoded
				if (entity.getPassword() != null && !entity.getPassword().startsWith("$2a$") && !entity.getPassword().startsWith("$2b$")) {
					entity.setPassword(passwordEncoder.encode(entity.getPassword()));
				}
			} else {
				entity.setUpdatedBy(username);
				entity.setUpdatedAt(LocalDateTime.now());
				// When updating, only encode password if it was changed
				if (entity.getPassword() != null && !entity.getPassword().startsWith("$2a$") && !entity.getPassword().startsWith("$2b$")) {
					entity.setPassword(passwordEncoder.encode(entity.getPassword()));
				}
			}

			return super.save(entity);
		} catch (Exception e) {
			log.error("Error saving user: {}", e.getMessage(), e);
			throw e;
		}
	}
}

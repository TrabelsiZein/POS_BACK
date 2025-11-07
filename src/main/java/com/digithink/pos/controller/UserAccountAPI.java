package com.digithink.pos.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.dto.CreateUserRequestDTO;
import com.digithink.pos.dto.UserAccountDTO;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.Role;
import com.digithink.pos.security.CurrentUserProvider;
import com.digithink.pos.service.UserAccountService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("user-account")
@Log4j2
public class UserAccountAPI {

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private CurrentUserProvider currentUserProvider;

	/**
	 * Check if current user is admin
	 */
	private boolean isAdmin() {
		try {
			UserAccount currentUser = currentUserProvider.getCurrentUser();
			return currentUser != null && currentUser.getRole() == Role.ADMIN;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get all users (admin only)
	 */
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		try {
			log.info("UserAccountAPI::getAllUsers");
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can access this resource"));
			}

			return ResponseEntity.ok(userAccountService.getAllUsers());
		} catch (Exception e) {
			log.error("UserAccountAPI::getAllUsers:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get user by ID (admin only)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		try {
			log.info("UserAccountAPI::getUserById: " + id);
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can access this resource"));
			}

			UserAccountDTO user = userAccountService.getUserById(id);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			log.error("UserAccountAPI::getUserById:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Create new user (admin only)
	 */
	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDTO request) {
		try {
			log.info("UserAccountAPI::createUser");
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can create users"));
			}

			// Validate request
			if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Username is required"));
			}
			if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
			}
			if (request.getRole() == null) {
				return ResponseEntity.badRequest().body(createErrorResponse("Role is required"));
			}

			UserAccountDTO createdUser = userAccountService.createUser(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (RuntimeException e) {
			log.error("UserAccountAPI::createUser:error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("UserAccountAPI::createUser:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Update user (admin only)
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
		try {
			log.info("UserAccountAPI::updateUser: " + id);
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can update users"));
			}

			UserAccount user = userAccountService.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

			// Update fields if provided
			if (request.containsKey("fullName")) {
				user.setFullName((String) request.get("fullName"));
			}
			if (request.containsKey("email")) {
				user.setEmail((String) request.get("email"));
			}
			if (request.containsKey("password")) {
				String password = (String) request.get("password");
				if (password != null && !password.trim().isEmpty()) {
					user.setPassword(password);
				}
			}
			if (request.containsKey("role")) {
				Object roleObj = request.get("role");
				if (roleObj instanceof String) {
					user.setRole(Role.valueOf((String) roleObj));
				} else if (roleObj instanceof Role) {
					user.setRole((Role) roleObj);
				}
			}
			if (request.containsKey("active")) {
				Object activeObj = request.get("active");
				if (activeObj instanceof Boolean) {
					user.setActive((Boolean) activeObj);
				} else if (activeObj instanceof String) {
					user.setActive(Boolean.parseBoolean((String) activeObj));
				}
			}

			UserAccount updatedUser = userAccountService.save(user);
			return ResponseEntity.ok(UserAccountDTO.fromEntity(updatedUser));
		} catch (RuntimeException e) {
			log.error("UserAccountAPI::updateUser:error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("UserAccountAPI::updateUser:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Delete user (admin only)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			log.info("UserAccountAPI::deleteUser: " + id);
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can delete users"));
			}

			UserAccount user = userAccountService.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

			// Prevent deleting yourself
			UserAccount currentUser = currentUserProvider.getCurrentUser();
			if (user.getId().equals(currentUser.getId())) {
				return ResponseEntity.badRequest().body(createErrorResponse("You cannot delete your own account"));
			}

			userAccountService.deleteById(id);
			return ResponseEntity.ok().body(createSuccessResponse("User deleted successfully"));
		} catch (RuntimeException e) {
			log.error("UserAccountAPI::deleteUser:error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("UserAccountAPI::deleteUser:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Toggle user active status (admin only)
	 */
	@PutMapping("/{id}/toggle-status")
	public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
		try {
			log.info("UserAccountAPI::toggleUserStatus: " + id);
			
			if (!isAdmin()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Only administrators can toggle user status"));
			}

			UserAccountDTO updatedUser = userAccountService.toggleUserStatus(id);
			return ResponseEntity.ok(updatedUser);
		} catch (RuntimeException e) {
			log.error("UserAccountAPI::toggleUserStatus:error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("UserAccountAPI::toggleUserStatus:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	// Helper methods from _BaseController
	private String getDetailedMessage(Exception e) {
		return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
	}

	private Map<String, String> createErrorResponse(String message) {
		return java.util.Map.of("error", message);
	}

	private Map<String, String> createSuccessResponse(String message) {
		return java.util.Map.of("message", message);
	}
}


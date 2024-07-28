package com.digithink.business_management.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.digithink.business_management.model.Permission;
import com.digithink.business_management.model.PermissionAction;
import com.digithink.business_management.model.PermissionPage;
import com.digithink.business_management.model.Role;
import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.repository.PermissionRepository;
import com.digithink.business_management.repository.RoleRepository;
import com.digithink.business_management.repository.UserAccountRepository;

@Component
public class ZZDataInitializer {

	@Autowired
	private UserAccountRepository userRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@PostConstruct
	public void init() {

		Permission readPermission = new Permission();
		readPermission.setAction(PermissionAction.READ);
		readPermission.setPage(PermissionPage.InventoryPostingGroup);
		permissionRepository.save(readPermission);

		Permission writePermission = new Permission();
		writePermission.setAction(PermissionAction.DELETE);
		writePermission.setPage(PermissionPage.ItemUnitOfMeasure);
		permissionRepository.save(writePermission);

		// Create admin role and assign permissions
		Role adminRole = new Role();
		adminRole.setName("ROLE_ADMIN");
		Set<Permission> adminPermissions = new HashSet<>();
		adminPermissions.add(readPermission);
		adminPermissions.add(writePermission);
		adminRole.setPermissions(adminPermissions);
		roleRepository.save(adminRole);

		// Create admin user and assign role
		UserAccount adminUser = new UserAccount();
		adminUser.setUsername("admin");
		adminUser.setPassword(passwordEncoder.encode("admin123"));
		adminUser.setEmail("admin@gmail.com");
		Set<Role> adminRoles = new HashSet<>();
		adminRoles.add(adminRole);
		adminUser.setRoles(adminRoles);
		userRepository.save(adminUser);

	}
}

package com.digithink.base_app.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.digithink.base_app.model.Permission;
import com.digithink.base_app.model.Role;
import com.digithink.base_app.model.UserAccount;
import com.digithink.base_app.model.enumeration.PermissionAction;
import com.digithink.base_app.model.enumeration.PermissionPage;
import com.digithink.base_app.repository.PermissionRepository;
import com.digithink.base_app.repository.RoleRepository;
import com.digithink.base_app.repository.UserAccountRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ZZDataInitializer {

	private UserAccountRepository userRepository;
	private PermissionRepository permissionRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() {
		if (userRepository.count() == 0) {
			initSysAdminUser(initSysAdminRole());
		}
	}

	private Role initSysAdminRole() {
		Role role = new Role();
		role.setName("SysAdmin");
		role.setDescription("System Administrator");
		for (PermissionPage iterable_element : Arrays.stream(PermissionPage.values()).collect(Collectors.toList())) {
			for (PermissionAction _iterable_element : Arrays.stream(PermissionAction.values())
					.collect(Collectors.toList())) {
				Permission permission = new Permission();
				permission.setPage(iterable_element);
				permission.setAction(_iterable_element);
				permissionRepository.save(permission);
			}
		}
		role.setPermissions(new HashSet<Permission>(permissionRepository.findAll()));
		return roleRepository.save(role);
	}

	private void initSysAdminUser(Role role) {
		UserAccount user = new UserAccount();
		user.setUsername("sys_admin");
		user.setPassword(passwordEncoder.encode("P@ssw0rd"));
		user.setEmail("sys_admin@gmail.com");
		user.setActive(true);
		Set<Role> sysAdminRoles = new HashSet<>();
		sysAdminRoles.add(role);
		user.setRoles(sysAdminRoles);
		userRepository.save(user);
	}
}

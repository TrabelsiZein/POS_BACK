package com.digithink.business_management.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.digithink.business_management.model.Permission;
import com.digithink.business_management.model.Role;
import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.model.enumeration.PermissionAction;
import com.digithink.business_management.model.enumeration.PermissionPage;
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
		initSysAdminUser(initSysAdminRole());
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

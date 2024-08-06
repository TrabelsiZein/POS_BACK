package com.digithink.business_management.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.Permission;
import com.digithink.business_management.model.Role;
import com.digithink.business_management.model.enumeration.PermissionPage;
import com.digithink.business_management.repository.PermissionRepository;
import com.digithink.business_management.repository.RoleRepository;

@Service
public class RoleService extends _BaseService<Role, Long> {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	protected JpaRepository<Role, Long> getRepository() {
		return roleRepository;
	}

	public List<PermissionPage> findAllPermissionsEnum() {
		return Arrays.stream(PermissionPage.values()).collect(Collectors.toList());
	}

	public List<Permission> findAllPermissionsDB() {
		return permissionRepository.findAll();
	}

	@Override
	protected JpaSpecificationExecutor<Role> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

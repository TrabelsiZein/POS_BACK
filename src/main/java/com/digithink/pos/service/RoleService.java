package com.digithink.vacation_app.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.Permission;
import com.digithink.vacation_app.model.Role;
import com.digithink.vacation_app.model.enumeration.PermissionPage;
import com.digithink.vacation_app.repository.PermissionRepository;
import com.digithink.vacation_app.repository.RoleRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class RoleService extends _BaseService<Role, Long> {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	protected _BaseRepository<Role, Long> getRepository() {
		return roleRepository;
	}

	public List<PermissionPage> findAllPermissionsEnum() {
		return Arrays.stream(PermissionPage.values()).collect(Collectors.toList());
	}

	public List<Permission> findAllPermissionsDB() {
		return permissionRepository.findAll();
	}

}

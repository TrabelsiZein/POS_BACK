package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.Permission;
import com.digithink.business_management.repository.PermissionRepository;

@Service
public class PermissionService extends _BaseService<Permission, Long> {

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	protected JpaRepository<Permission, Long> getRepository() {
		return permissionRepository;
	}

}

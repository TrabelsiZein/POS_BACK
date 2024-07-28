package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Role;
import com.digithink.business_management.repository.RoleRepository;

public class RoleService extends _BaseService<Role, Long> {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	protected JpaRepository<Role, Long> getRepository() {
		return roleRepository;
	}

}

package com.digithink.business_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.digithink.business_management.model.system.Role;

public interface RoleRepository extends _BaseSysRepository<Role, Long> {

	@Query("select new com.digithink.business_management.model.system.Role(r.id,r.name,r.description) from Role r")
	List<Role> findAll();
}

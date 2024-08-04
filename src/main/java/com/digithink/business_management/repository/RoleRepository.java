package com.digithink.business_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digithink.business_management.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query("select new com.digithink.business_management.model.Role(r.id,r.name,r.description) from Role r")
	List<Role> findAll();
}

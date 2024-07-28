package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}

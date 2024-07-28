package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}

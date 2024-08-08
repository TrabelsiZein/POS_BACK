package com.digithink.business_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digithink.business_management.dto.AbilityDTO;
import com.digithink.business_management.model.enumeration.PermissionAction;
import com.digithink.business_management.model.enumeration.PermissionPage;
import com.digithink.business_management.model.system.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	@Query("SELECT new com.digithink.business_management.dto.AbilityDTO(p.page, p.action) FROM UserAccount u "
			+ "JOIN u.roles r " + "JOIN r.permissions p WHERE u.username = :username")
	List<AbilityDTO> getUserPermissions(@Param("username") String username);

	Permission findByPageAndAction(PermissionPage page, PermissionAction action);
}

package com.digithink.base_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digithink.base_app.dto.AbilityDTO;
import com.digithink.base_app.model.Permission;
import com.digithink.base_app.model.enumeration.PermissionAction;
import com.digithink.base_app.model.enumeration.PermissionPage;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	@Query("SELECT new com.digithink.base_app.dto.AbilityDTO(p.page, p.action) FROM UserAccount u "
			+ "JOIN u.roles r " + "JOIN r.permissions p WHERE u.username = :username")
	List<AbilityDTO> getUserPermissions(@Param("username") String username);

	Permission findByPageAndAction(PermissionPage page, PermissionAction action);
}

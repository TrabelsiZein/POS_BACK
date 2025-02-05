package com.digithink.base_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.base_app.model.Role;
import com.digithink.base_app.service.RoleService;

@RestController
@RequestMapping("role")
public class RoleAPI extends _BaseController<Role, Long, RoleService> {

	@GetMapping("permissions-enum")
	public ResponseEntity<?> getAllPermissions() {
		try {
			return ResponseEntity.ok(service.findAllPermissionsEnum());
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("permissions-db")
	public ResponseEntity<?> getAllPermissionss() {
		try {
			return ResponseEntity.ok(service.findAllPermissionsDB());
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}

package com.digithink.business_management.dto;

import java.util.HashSet;
import java.util.Set;

import com.digithink.business_management.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccountDTO {

	private Long id;
	private String username;
	private String email;
	private Set<Role> roles = new HashSet<>();
}

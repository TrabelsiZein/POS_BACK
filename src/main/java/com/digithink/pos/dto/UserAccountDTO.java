package com.digithink.pos.dto;

import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDTO {

	private Long id;
	private String username;
	private String fullName;
	private String email;
	private Boolean active;
	private Role role;
	
	public static UserAccountDTO fromEntity(UserAccount user) {
		UserAccountDTO dto = new UserAccountDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setFullName(user.getFullName());
		dto.setEmail(user.getEmail());
		dto.setActive(user.getActive());
		dto.setRole(user.getRole());
		return dto;
	}
}

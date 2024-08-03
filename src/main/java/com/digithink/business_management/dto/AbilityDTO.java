package com.digithink.business_management.dto;

import com.digithink.business_management.model.enumeration.PermissionAction;
import com.digithink.business_management.model.enumeration.PermissionPage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AbilityDTO {

	private String action;
	private String subject;

	public AbilityDTO(PermissionPage PAGE, PermissionAction ACTION) {
		this.action = ACTION.name();
		this.subject = PAGE.name();
	}
}

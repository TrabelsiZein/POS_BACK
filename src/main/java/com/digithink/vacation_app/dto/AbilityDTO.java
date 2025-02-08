package com.digithink.vacation_app.dto;

import com.digithink.vacation_app.model.enumeration.PermissionAction;
import com.digithink.vacation_app.model.enumeration.PermissionPage;

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

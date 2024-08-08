package com.digithink.business_management.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.digithink.business_management.model.enumeration.PermissionAction;
import com.digithink.business_management.model.enumeration.PermissionPage;

import lombok.Data;

@Entity
@Data
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private PermissionPage page;

	@Column(nullable = false)
	private PermissionAction action;

}

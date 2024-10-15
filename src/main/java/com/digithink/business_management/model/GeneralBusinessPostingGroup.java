package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class GeneralBusinessPostingGroup extends _BaseEntity {

	@Column(length = 20, nullable = false, unique = true)
	private String no;
	@Column(length = 100, nullable = false, unique = true)
	private String description;
	// VatBusinessPostingGroup Entity
	@Column(length = 20)
	private String defVatBusPostingGroup;
}

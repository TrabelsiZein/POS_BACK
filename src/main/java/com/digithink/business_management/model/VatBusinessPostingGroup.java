package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class VatBusinessPostingGroup extends _BaseEntity {

	@Column(unique = true, nullable = false, length = 20)
	private String no;
	@Column(unique = true, nullable = false, length = 100)
	private String description;

}

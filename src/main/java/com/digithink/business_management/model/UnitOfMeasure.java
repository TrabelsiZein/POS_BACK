package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class UnitOfMeasure extends _BaseEntity {

	@Column(unique = true, nullable = false)
	private String code;
	@Column(unique = true, nullable = false)
	private String description;
}

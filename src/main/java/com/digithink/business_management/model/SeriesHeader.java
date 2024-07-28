package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SeriesHeader extends _BaseEntity {

	@Column(unique = true, nullable = false, length = 20)
	private String code;
	@Column(unique = true, nullable = false, length = 100)
	private String description;
	private Boolean defaultNo;
	private Boolean manualNo;
	private Boolean chronologicalOrder;
	private SeriesHeaderType type;

}

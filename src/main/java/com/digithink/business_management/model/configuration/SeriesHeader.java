package com.digithink.business_management.model.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.model.enumeration.SeriesHeaderType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SeriesHeader extends _BaseEntity {

	@Column(unique = true, nullable = false, length = 20)
	private String no;
	@Column(unique = true, nullable = false, length = 100)
	private String description;
	private Boolean defaultNo;
	private Boolean manualNo;
	private Boolean chronologicalOrder;
	private SeriesHeaderType type;

}

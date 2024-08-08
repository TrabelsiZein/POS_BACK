package com.digithink.business_management.model.posting_group;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;

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

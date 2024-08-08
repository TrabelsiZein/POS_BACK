package com.digithink.business_management.model.posting_group;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class GeneralProductPostingGroup extends _BaseEntity {

	@Column(length = 20, nullable = false, unique = true)
	private String no;
	@Column(length = 100, nullable = false, unique = true)
	private String description;
	// VatProductPostingGroup Entity
	@Column(length = 20)
	private String defVatProdPostingGroup;
}

package com.digithink.business_management.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class InventoryPostingGroup extends _BaseEntity {

	private String code;
	private String description;

}

package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class InventoryPostingSetup extends _BaseEntity {

	// Location Entity
	@Column(length = 20)
	private String locationCode;
	// InventoryPostingGroup Entity
	@Column(length = 20)
	private String invtPostingGroupCode;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String inventoryAccount;

}

package com.digithink.business_management.model.posting_setup;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class VatPostingSetup extends _BaseEntity {

	private Double vat;

	// VatBusinessPostingGroup Entity
	@Column(length = 20)
	private String vatBusPostingGroup;
	// VatProductPostingGroup Entity
	@Column(length = 20)
	private String vatProdPostingGroup;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String salesVatAccount;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String purchaseVatAccount;

}

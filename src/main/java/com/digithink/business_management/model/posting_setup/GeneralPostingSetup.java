package com.digithink.business_management.model.posting_setup;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class GeneralPostingSetup extends _BaseEntity {

	// GeneralBusinessPostingGroup Entity
	@Column(length = 20)
	private String genBusPostingGroup;
	// GeneralProductPostingGroup Entity
	@Column(length = 20)
	private String genProdPostingGroup;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String salesAccount;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String purchaseAccount;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String salesCreditMemoAccount;
	// GeneralLedgerAccount Entity
	@Column(length = 20)
	private String purchaseCreditMemoAccount;

}

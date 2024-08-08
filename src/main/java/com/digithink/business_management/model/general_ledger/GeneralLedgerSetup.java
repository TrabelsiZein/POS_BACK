package com.digithink.business_management.model.general_ledger;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class GeneralLedgerSetup extends _BaseEntity {

	private LocalDate allowPostingFrom;
	private LocalDate allowPostingTo;
	private Double inventoryRoundingPrecision;
	private Double stampDutyAmount;
	// Currency Entity
	@Column(length = 20)
	private String lCYNo;
	// G/L Account Entity
	@Column(length = 20)
	private String salesStampDutyAccount;
	// G/L Account Entity
	@Column(length = 20)
	private String purchaseStampDutyAccount;

}

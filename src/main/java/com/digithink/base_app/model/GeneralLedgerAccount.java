package com.digithink.base_app.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.digithink.base_app.model.enumeration.GLAccountCategory;
import com.digithink.base_app.model.enumeration.GLAccountType;
import com.digithink.base_app.model.enumeration.GLDebitOrCredit;
import com.digithink.base_app.model.enumeration.GLManagementOrBalance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class GeneralLedgerAccount extends _BaseEntity {

	@Column(length = 20, nullable = false, unique = true)
	private String no;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "accountType", nullable = false)
	private GLAccountType accountType;

	@Column(name = "mainAxisCode1", length = 20)
	private String mainAxisCode1;

	@Column(name = "mainAxisCode2", length = 20)
	private String mainAxisCode2;

	@Enumerated(EnumType.STRING)
	@Column(name = "accountCategory", nullable = false)
	private GLAccountCategory accountCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "managementOrBalance", nullable = false)
	private GLManagementOrBalance managementOrBalance;

	@Enumerated(EnumType.STRING)
	@Column(name = "debitOrCredit", nullable = false)
	private GLDebitOrCredit debitOrCredit;

	@Column(name = "blocked", nullable = false)
	private boolean blocked;

	@Column(name = "directImputation", nullable = false)
	private boolean directImputation;

	@Column(name = "totalization", length = 250)
	private String totalization;

//	// VatBusinessPostingGroup Entity
//	@Column(length = 20)
//	private String vatBusinessPostingGroup;
//
//	// VatProductPostingGroup Entity
//	@Column(length = 20)
//	private String vatProductPostingGroup;
//
//	// GeneralBusinessPostingGroupEntity Entity
//	@Column(length = 20)
//	private String genBusinessPostingGroup;
//
//	// GeneralProductPostingGroupEntity
//	@Column(length = 20)
//	private String genProductPostingGroup;

	// FlowFields

	@Transient
	private LocalDate dateFilter;

	@Transient
	private String mainAxisFilter1;

	@Transient
	private String mainAxisFilter2;

	@Transient
	private BigDecimal balanceTo;

	@Transient
	private BigDecimal periodBalance;

	@Transient
	private BigDecimal balance;

	@Transient
	private BigDecimal debitAmount;

	@Transient
	private BigDecimal creditAmount;

	@Transient
	private BigDecimal vatAmount;

}

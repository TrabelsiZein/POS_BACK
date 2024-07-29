package com.digithink.business_management.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.digithink.business_management.model.enumeration.GLAccountCategory;
import com.digithink.business_management.model.enumeration.GLAccountType;
import com.digithink.business_management.model.enumeration.GLDebitOrCredit;
import com.digithink.business_management.model.enumeration.GLManagementOrBalance;

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

	@Column(name = "lastModified", nullable = false)
	private LocalDateTime lastModified;

	@Column(name = "totalization", length = 250)
	private String totalization;

	// ........ Entity
	@Column(name = "taxGroupCode", length = 20)
	private String taxGroupCode;

	// ........ Entity
	@Column(name = "marketVATAccountingGroup", length = 20)
	private String marketVATAccountingGroup;

	// ........ Entity
	@Column(name = "productVATAccountingGroup", length = 20)
	private String productVATAccountingGroup;

	// ........ Entity
	@Column(name = "marketAccountingGroup", length = 20)
	private String marketAccountingGroup;

	// ........ Entity
	@Column(name = "productAccountingGroup", length = 20)
	private String productAccountingGroup;

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

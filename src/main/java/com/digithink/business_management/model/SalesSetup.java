package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model.enumeration.DefaultAccountingDate;
import com.digithink.business_management.model.enumeration.DefaultQuantityToShip;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SalesSetup extends _BaseEntity {

	private Boolean creditAlert;
	private Boolean outOfStockAlert;
	private Boolean mandatoryExtDocNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String customerNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String currencyNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String orderNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String invoiceNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registeredInvoiceNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String creditMemoNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registredCreditMemoNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registeredShipmentNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String openOrderNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String returnNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registeredReturnReceiptNo;
	private Boolean calculateInvoiceDiscount;
	private Boolean allowVATDifference;
	private DefaultAccountingDate defaultAccountingDate;
	private DefaultQuantityToShip defaultQuantityToShip;
	private Boolean archiveCurrencies;
	private Boolean archiveOrders;
	private Boolean archiveOpenOrders;
	private Boolean archiveReturns;

}

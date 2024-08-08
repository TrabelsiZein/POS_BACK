package com.digithink.business_management.model.setup;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.model.enumeration.DefaultAccountingDate;
import com.digithink.business_management.model.enumeration.DefaultQuantityToReceive;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class PurchaseSetup extends _BaseEntity {

	private Boolean mandatoryExtDocNo;
	private Boolean calculateInvoiceDiscount;
	private Boolean allowVATDifference;
	private DefaultAccountingDate defaultAccountingDate;
	private DefaultQuantityToReceive defaultQuantityToReceive;
	private Boolean archiveCurrencies;
	private Boolean archiveOrders;
	private Boolean archiveOpenOrders;
	private Boolean archiveReturns;
	// SeriesHeader Entity
	@Column(length = 20)
	private String vendorNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String requestQuoteNo;
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
	private String registeredCreditMemoNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registeredReceiptNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String openOrderNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String returnNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String registeredReturnShipmentNo;
}

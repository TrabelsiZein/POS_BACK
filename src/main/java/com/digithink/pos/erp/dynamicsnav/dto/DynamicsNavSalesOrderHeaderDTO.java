package com.digithink.pos.erp.dynamicsnav.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicsNavSalesOrderHeaderDTO {

	@JsonProperty("Document_Type")
	private String documentType = "Order";

	@JsonProperty("No")
	private String documentNo;

	@JsonProperty("Sell_to_Customer_No")
	private String sellToCustomerNo;

	@JsonProperty("Sell_to_Customer_Name")
	private String sellToCustomerName;

	@JsonProperty("Responsibility_Center")
	private String responsibilityCenter;

	@JsonProperty("Location_Code")
	private String locationCode;

	@JsonProperty("Posting_Date")
	private LocalDate postingDate;

	@JsonProperty("Fence_No")
	private String fenceNo;

	@JsonProperty("POS_Document_No")
	private String posDocumentNo;

	@JsonProperty("POS_Invoice")
	private Boolean posInvoice = false;

	@JsonProperty("Fiscal_Registration")
	private String fiscalRegistration;

	@JsonProperty("Discount_Percent")
	private Double discountPercent;

	@JsonProperty("POS_Order")
	private Boolean posOrder = false;

	// Getters and setters
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	@JsonIgnore
	public String getDocumentNo() {
		return documentNo;
	}

	// Setter without @JsonProperty to prevent serialization
	// We'll manually set this from the response
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getSellToCustomerNo() {
		return sellToCustomerNo;
	}

	public void setSellToCustomerNo(String sellToCustomerNo) {
		this.sellToCustomerNo = sellToCustomerNo;
	}

	public String getSellToCustomerName() {
		return sellToCustomerName;
	}

	public void setSellToCustomerName(String sellToCustomerName) {
		this.sellToCustomerName = sellToCustomerName;
	}

	public String getResponsibilityCenter() {
		return responsibilityCenter;
	}

	public void setResponsibilityCenter(String responsibilityCenter) {
		this.responsibilityCenter = responsibilityCenter;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public LocalDate getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	public String getFenceNo() {
		return fenceNo;
	}

	public void setFenceNo(String fenceNo) {
		this.fenceNo = fenceNo;
	}

	public String getPosDocumentNo() {
		return posDocumentNo;
	}

	public void setPosDocumentNo(String posDocumentNo) {
		this.posDocumentNo = posDocumentNo;
	}

	public Boolean getPosInvoice() {
		return posInvoice;
	}

	public void setPosInvoice(Boolean posInvoice) {
		this.posInvoice = posInvoice;
	}

	public String getFiscalRegistration() {
		return fiscalRegistration;
	}

	public void setFiscalRegistration(String fiscalRegistration) {
		this.fiscalRegistration = fiscalRegistration;
	}

	public Double getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(Double discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Boolean getPosOrder() {
		return posOrder;
	}

	public void setPosOrder(Boolean posOrder) {
		this.posOrder = posOrder;
	}
}

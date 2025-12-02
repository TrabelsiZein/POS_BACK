package com.digithink.pos.erp.dynamicsnav.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicsNavSalesOrderLineDTO {

	@JsonProperty("Document_Type")
	private String documentType = "Order";

	@JsonProperty("Document_No")
	private String documentNo;

	@JsonProperty("Line_No")
	private Integer lineNo;

	// Type is read-only in NAV, so it should not be serialized in requests
	// @JsonIgnore on getter prevents serialization
	@JsonProperty("Type")
	private String type = "Item";

	@JsonProperty("No")
	private String no;

	@JsonProperty("Quantity")
	private Double quantity;

	@JsonProperty("Unit_Price")
	private Double unitPrice;

	@JsonProperty("Line_Discount_Percent")
	private Double lineDiscountPercent;

	// Getters and setters
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getLineDiscountPercent() {
		return lineDiscountPercent;
	}

	public void setLineDiscountPercent(Double lineDiscountPercent) {
		this.lineDiscountPercent = lineDiscountPercent;
	}
}

package com.digithink.pos.erp.dto;

import java.math.BigDecimal;

public class ErpItemDTO {

	private String externalId;
	private String code;
	private String name;
	private String description;
	private String familyExternalId;
	private String subFamilyExternalId;
	private BigDecimal salesPrice;
	private BigDecimal costPrice;
	private Boolean active;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFamilyExternalId() {
		return familyExternalId;
	}

	public void setFamilyExternalId(String familyExternalId) {
		this.familyExternalId = familyExternalId;
	}

	public String getSubFamilyExternalId() {
		return subFamilyExternalId;
	}

	public void setSubFamilyExternalId(String subFamilyExternalId) {
		this.subFamilyExternalId = subFamilyExternalId;
	}

	public BigDecimal getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(BigDecimal salesPrice) {
		this.salesPrice = salesPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}


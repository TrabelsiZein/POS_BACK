package com.digithink.pos.erp.dto;

import java.time.OffsetDateTime;

public class ErpItemBarcodeDTO implements ErpTimestamped {

	private String itemExternalId;
	private String barcode;
	private String unitOfMeasure;
	private Boolean primaryBarcode;
	private OffsetDateTime lastModifiedAt;

	public String getItemExternalId() {
		return itemExternalId;
	}

	public void setItemExternalId(String itemExternalId) {
		this.itemExternalId = itemExternalId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public Boolean getPrimaryBarcode() {
		return primaryBarcode;
	}

	public void setPrimaryBarcode(Boolean primaryBarcode) {
		this.primaryBarcode = primaryBarcode;
	}

	@Override
	public OffsetDateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(OffsetDateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
}


package com.digithink.pos.erp.dto;

public class ErpItemBarcodeDTO {

	private String itemExternalId;
	private String barcode;
	private String unitOfMeasure;
	private Boolean primaryBarcode;

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
}


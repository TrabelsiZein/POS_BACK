package com.digithink.pos.erp.dynamicsnav.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicsNavBarcodeDTO {

	@JsonProperty("Item_No")
	private String itemNo;

	@JsonProperty("Barcode")
	private String barcode;

	@JsonProperty("Unit_of_Measure_Code")
	private String unitOfMeasureCode;

	@JsonProperty("IsDefaultBarcode")
	private Boolean defaultBarcode;

	@JsonProperty("Primary")
	private Boolean primary;

	@JsonProperty("Modified_At")
	private OffsetDateTime modifiedAt;
}


package com.digithink.pos.erp.dynamicsnav.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicsNavStockKeepingUnitDTO {

	@JsonProperty("Item_No")
	private String itemNo;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("Item_Category_Code")
	private String itemCategoryCode;

	@JsonProperty("Product_Group_Code")
	private String productGroupCode;

	@JsonProperty("Unit_Price")
	private BigDecimal unitPrice;

	@JsonProperty("Unit_Cost")
	private BigDecimal unitCost;

	@JsonProperty("Blocked")
	private Boolean blocked;

	@JsonProperty("Modified_At")
	private OffsetDateTime modifiedAt;
}


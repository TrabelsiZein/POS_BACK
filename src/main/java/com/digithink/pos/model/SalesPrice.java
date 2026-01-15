package com.digithink.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SalesPrice entity - represents sales prices from Dynamics NAV
 */
@Entity
@Table(name = "sales_price", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "item_no", "sales_type", "sales_code", "responsibility_center",
				"starting_date", "currency_code", "variant_code", "unit_of_measure_code", "minimum_quantity" }) })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SalesPrice extends _BaseEntity {

	@Column(name = "erp_external_id")
	private String erpExternalId;

	@Column(name = "item_no", nullable = false)
	private String itemNo;

	@Column(name = "sales_type", nullable = false)
	private String salesType;

	@Column(name = "sales_code", nullable = false)
	private String salesCode;

	@Column(name = "unit_price")
	private Double unitPrice;

	@Column(name = "responsibility_center", nullable = false)
	private String responsibilityCenter;

	@Column(name = "responsibility_center_type")
	private String responsibilityCenterType;

	@Column(name = "starting_date", nullable = false)
	private String startingDate;

	@Column(name = "ending_date")
	private String endingDate;

	@Column(name = "currency_code", nullable = false)
	private String currencyCode;

	@Column(name = "variant_code", nullable = false)
	private String variantCode;

	@Column(name = "unit_of_measure_code", nullable = false)
	private String unitOfMeasureCode;

	@Column(name = "minimum_quantity", nullable = false)
	private Double minimumQuantity;
}


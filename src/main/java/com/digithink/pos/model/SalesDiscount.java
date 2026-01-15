package com.digithink.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SalesDiscount entity - represents sales discounts from Dynamics NAV
 */
@Entity
@Table(name = "sales_discount", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "type", "code", "sales_type", "sales_code", "responsibility_center",
				"starting_date", "auxiliary_index1", "auxiliary_index2", "auxiliary_index3", "auxiliary_index4" }) })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SalesDiscount extends _BaseEntity {

	@Column(name = "erp_external_id")
	private String erpExternalId;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "sales_type", nullable = false)
	private String salesType;

	@Column(name = "sales_code", nullable = false)
	private String salesCode;

	@Column(name = "responsibility_center_type")
	private String responsibilityCenterType;

	@Column(name = "responsibility_center", nullable = false)
	private String responsibilityCenter;

	@Column(name = "starting_date", nullable = false)
	private String startingDate;

	@Column(name = "ending_date")
	private String endingDate;

	@Column(name = "line_discount")
	private Double lineDiscount;

	@Column(name = "auxiliary_index1", nullable = false)
	private String auxiliaryIndex1;

	@Column(name = "auxiliary_index2", nullable = false)
	private String auxiliaryIndex2;

	@Column(name = "auxiliary_index3", nullable = false)
	private String auxiliaryIndex3;

	@Column(name = "auxiliary_index4", nullable = false)
	private Integer auxiliaryIndex4;
}


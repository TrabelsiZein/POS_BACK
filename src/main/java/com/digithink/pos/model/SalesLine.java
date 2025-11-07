package com.digithink.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Sales line entity - line items for sales
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SalesLine extends _BaseEntity {

	@ManyToOne
	@JoinColumn(name = "sales_header_id", nullable = false)
	@JsonIgnore
	private SalesHeader salesHeader;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Double unitPrice;

	private Double discountPercentage;

	private Double discountAmount;

	@Column(nullable = false)
	private Double lineTotal;

	private String notes;
	
	// Transient fields for convenience
	private transient Long salesHeaderId;
	
	private transient Long itemId;
}


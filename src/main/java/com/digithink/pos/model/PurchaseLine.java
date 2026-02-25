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
 * Purchase line entity - line items for a purchase.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PurchaseLine extends _BaseEntity {

	@ManyToOne
	@JoinColumn(name = "purchase_header_id", nullable = false)
	@JsonIgnore
	private PurchaseHeader purchaseHeader;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Double unitPrice;

	@Column(nullable = false)
	private Double lineTotal;

	private Double lineTotalIncludingVat;
}

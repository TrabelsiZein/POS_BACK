package com.digithink.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.digithink.pos.model.enumeration.ItemType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Item entity - represents products/services in the POS system
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Item extends _BaseEntity {

	@Column(name = "erp_external_id")
	private String erpExternalId;

	@Column(nullable = false, unique = true)
	private String itemCode;

	@Column(nullable = false)
	private String name;

	private String description;

	@Enumerated(EnumType.STRING)
	private ItemType type = ItemType.PRODUCT;

	private Double unitPrice;

	public Integer defaultVAT;

	private Double costPrice;

	private Integer stockQuantity;

	private Integer minStockLevel;

	private String barcode;

	private String imageUrl;

	private String unitOfMeasure;

	private String category;

	private String brand;

	@Column(name = "item_disc_group")
	private String itemDiscGroup;

	@Column(name = "maximum_authorized_discount")
	private Double maximumAuthorizedDiscount; // Maximum discount percentage allowed for this item

	@ManyToOne
	@JoinColumn(name = "item_family_id")
	private ItemFamily itemFamily;

	@ManyToOne
	@JoinColumn(name = "item_sub_family_id")
	private ItemSubFamily itemSubFamily;

	/**
	 * When false, item is hidden from POS (e.g. system items like Tax Stamp).
	 * Default true for normal products.
	 */
	@Column(name = "show_in_pos", nullable = false)
	private Boolean showInPos = true;
}

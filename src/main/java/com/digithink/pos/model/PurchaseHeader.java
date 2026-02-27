package com.digithink.pos.model;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.digithink.pos.model.enumeration.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Purchase header entity - represents a purchase from a vendor (standalone mode only).
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PurchaseHeader extends _BaseEntity {

	@Column(nullable = false, unique = true)
	private String purchaseNumber;

	@Column(nullable = false)
	private LocalDateTime purchaseDate = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@ManyToOne
	@JoinColumn(name = "created_by_user")
	private UserAccount createdByUser;

	@Enumerated(EnumType.STRING)
	private TransactionStatus status = TransactionStatus.COMPLETED;

	private Double subtotal;

	private Double taxAmount;

	private Double discountAmount;

	@Column(name = "total_amount")
	private Double totalAmount;

	/** Amount paid to vendor (standalone). Null = unpaid. */
	private Double paidAmount;

	/** Date when (partial or full) payment was recorded (standalone). */
	private LocalDateTime paidDate;

	private String notes;

	@OneToMany(mappedBy = "purchaseHeader", fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<PurchaseLine> purchaseLines = new ArrayList<>();
}

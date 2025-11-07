package com.digithink.pos.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.digithink.pos.model.enumeration.TransactionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Sales header entity - represents sales orders/invoices/tickets
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SalesHeader extends _BaseEntity {

	@Column(nullable = false, unique = true)
	private String salesNumber;

	@Column(nullable = false)
	private LocalDateTime salesDate = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "created_by_user")
	private UserAccount createdByUser;

	@ManyToOne
	@JoinColumn(name = "cashier_session_id")
	private CashierSession cashierSession;

	@Enumerated(EnumType.STRING)
	private TransactionStatus status = TransactionStatus.PENDING;

	private Double subtotal;

	private Double taxAmount;

	private Double discountAmount;

	private Double totalAmount;

	private Double paidAmount;

	private Double changeAmount;

	private String paymentReference;

	private String notes;

	private LocalDateTime completedDate;

	// Transient field for display purposes
	@Transient
	private Long customerId;

	// Transient field for display purposes
	@Transient
	private Long createdByUserId;

	// Transient field for display purposes
	@Transient
	private Long cashierSessionId;
}

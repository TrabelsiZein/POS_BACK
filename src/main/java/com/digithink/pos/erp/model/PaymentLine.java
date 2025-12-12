package com.digithink.pos.erp.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.digithink.pos.model.Payment;
import com.digithink.pos.model._BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Payment line entity for ERP synchronization tracking Represents individual
 * payment lines within a payment header
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PaymentLine extends _BaseEntity {

	@ManyToOne
	@JoinColumn(name = "payment_header_id", nullable = false)
	private PaymentHeader paymentHeader;

	@ManyToOne
	@JoinColumn(name = "payment_id")
	private Payment payment; // Reference to original Payment entity

	@Column(nullable = false)
	private String custNo; // Customer code

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private String fenceNo; // Session number

	@Column(nullable = false)
	private String ticketNo; // POS ticket number

	private String titleNumber;

	private LocalDate dueDate;

	private String drawerName;

	@Column(nullable = false)
	private Boolean synched = false;
}

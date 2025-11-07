package com.digithink.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import com.digithink.pos.model.enumeration.PaymentMethodType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Payment method entity - payment methods accepted in POS
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PaymentMethod extends _BaseEntity {

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	private PaymentMethodType type;

	private String description;

	private Double processingFee;

	private Boolean requiresConfirmation = false;

	private Boolean active = true;

	// Additional required data flags
	private Boolean requireTitleNumber = false;

	private Boolean requireDueDate = false;

	private Boolean requireDrawerName = false;

	private Boolean requireIssuingBank = false;
}


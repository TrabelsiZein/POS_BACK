package com.digithink.pos.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.digithink.pos.model.enumeration.SessionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Cashier session entity - represents a cashier's shift/session
 * When POS user opens his shift, creates a session
 * When closing, counts the money and closes the session
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CashierSession extends _BaseEntity {

	@Column(nullable = false, unique = true)
	private String sessionNumber;

	@ManyToOne
	@JoinColumn(name = "cashier_id", nullable = false)
	private UserAccount cashier;

	@Column(nullable = false)
	private LocalDateTime openedAt;

	private LocalDateTime closedAt;

	@Enumerated(EnumType.STRING)
	private SessionStatus status = SessionStatus.OPENED;

	// Opening cash fund (font de caisse)
	@Column(nullable = false)
	private Double openingCash;

	// Real cash - what should be in cashier (openingCash + cash sales - change given)
	private Double realCash;

	// POS user closure cash - cash counted by POS user when closing session
	private Double posUserClosureCash;

	// Responsible closure cash - cash counted by responsible user when verifying
	private Double responsibleClosureCash;

	// Actual counted cash at closing (deprecated - use posUserClosureCash instead)
	private Double actualCash;

	// Difference between expected and actual (deprecated - calculate from realCash and posUserClosureCash)
	private Double cashDifference;

	// Responsible user who verified the count
	@ManyToOne
	@JoinColumn(name = "verified_by_user")
	private UserAccount verifiedBy;

	private LocalDateTime verifiedAt;

	private String verificationNotes;

	private String cashCountingDetails; // e.g., "5 pieces of 10 dinar, 6 pieces of 50 dinar, 2 checks..."
}


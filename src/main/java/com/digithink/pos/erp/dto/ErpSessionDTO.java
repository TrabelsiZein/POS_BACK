package com.digithink.pos.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ErpSessionDTO {

	private String externalId;
	private String sessionNumber;
	private String cashierExternalId; // Cashier user external ID from ERP
	private String cashierUsername;
	private LocalDateTime openedAt;
	private LocalDateTime closedAt;
	private String status; // OPENED, CLOSED, TERMINATED
	private BigDecimal openingCash;
	private BigDecimal realCash;
	private BigDecimal posUserClosureCash;
	private BigDecimal responsibleClosureCash;
	private String verifiedByUsername; // Username of user who verified
	private LocalDateTime verifiedAt;
	private String verificationNotes;

	// Additional fields for ERP export
	private String responsibilityCenter;
	private String locationExternalId;
	
	// Statistics for ERP export
	private Integer ticketCount;
	private Integer returnCount; // Simple returns (cashed) only
	private Integer totalReturnCount; // All returns (simple + voucher)
	private Double returnAmount;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	public String getCashierExternalId() {
		return cashierExternalId;
	}

	public void setCashierExternalId(String cashierExternalId) {
		this.cashierExternalId = cashierExternalId;
	}

	public String getCashierUsername() {
		return cashierUsername;
	}

	public void setCashierUsername(String cashierUsername) {
		this.cashierUsername = cashierUsername;
	}

	public LocalDateTime getOpenedAt() {
		return openedAt;
	}

	public void setOpenedAt(LocalDateTime openedAt) {
		this.openedAt = openedAt;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getOpeningCash() {
		return openingCash;
	}

	public void setOpeningCash(BigDecimal openingCash) {
		this.openingCash = openingCash;
	}

	public BigDecimal getRealCash() {
		return realCash;
	}

	public void setRealCash(BigDecimal realCash) {
		this.realCash = realCash;
	}

	public BigDecimal getPosUserClosureCash() {
		return posUserClosureCash;
	}

	public void setPosUserClosureCash(BigDecimal posUserClosureCash) {
		this.posUserClosureCash = posUserClosureCash;
	}

	public BigDecimal getResponsibleClosureCash() {
		return responsibleClosureCash;
	}

	public void setResponsibleClosureCash(BigDecimal responsibleClosureCash) {
		this.responsibleClosureCash = responsibleClosureCash;
	}

	public String getVerifiedByUsername() {
		return verifiedByUsername;
	}

	public void setVerifiedByUsername(String verifiedByUsername) {
		this.verifiedByUsername = verifiedByUsername;
	}

	public LocalDateTime getVerifiedAt() {
		return verifiedAt;
	}

	public void setVerifiedAt(LocalDateTime verifiedAt) {
		this.verifiedAt = verifiedAt;
	}

	public String getVerificationNotes() {
		return verificationNotes;
	}

	public void setVerificationNotes(String verificationNotes) {
		this.verificationNotes = verificationNotes;
	}

	public String getResponsibilityCenter() {
		return responsibilityCenter;
	}

	public void setResponsibilityCenter(String responsibilityCenter) {
		this.responsibilityCenter = responsibilityCenter;
	}

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}

	public Integer getReturnCount() {
		return returnCount;
	}

	public void setReturnCount(Integer returnCount) {
		this.returnCount = returnCount;
	}

	public Double getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(Double returnAmount) {
		this.returnAmount = returnAmount;
	}

	public String getLocationExternalId() {
		return locationExternalId;
	}

	public void setLocationExternalId(String locationExternalId) {
		this.locationExternalId = locationExternalId;
	}

	public Integer getTotalReturnCount() {
		return totalReturnCount;
	}

	public void setTotalReturnCount(Integer totalReturnCount) {
		this.totalReturnCount = totalReturnCount;
	}
}


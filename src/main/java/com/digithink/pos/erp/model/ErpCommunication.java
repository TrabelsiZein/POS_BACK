package com.digithink.pos.erp.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.digithink.pos.erp.enumeration.ErpCommunicationStatus;
import com.digithink.pos.erp.enumeration.ErpSyncOperation;
import com.digithink.pos.model._BaseEntity;

/**
 * Entity used to track every communication exchange with an external ERP.
 */
@Entity
@Table(name = "ERP_COMMUNICATION")
public class ErpCommunication extends _BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "operation", nullable = false, length = 64)
	private ErpSyncOperation operation;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 32)
	private ErpCommunicationStatus status;

	@Lob
	@Column(name = "request_payload")
	private String requestPayload;

	@Lob
	@Column(name = "response_payload")
	private String responsePayload;

	@Column(name = "external_reference", length = 128)
	private String externalReference;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "duration_ms")
	private Long durationMs;

	@Lob
	@Column(name = "error_message")
	private String errorMessage;

	public ErpSyncOperation getOperation() {
		return operation;
	}

	public void setOperation(ErpSyncOperation operation) {
		this.operation = operation;
	}

	public ErpCommunicationStatus getStatus() {
		return status;
	}

	public void setStatus(ErpCommunicationStatus status) {
		this.status = status;
	}

	public String getRequestPayload() {
		return requestPayload;
	}

	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}

	public String getResponsePayload() {
		return responsePayload;
	}

	public void setResponsePayload(String responsePayload) {
		this.responsePayload = responsePayload;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public Long getDurationMs() {
		return durationMs;
	}

	public void setDurationMs(Long durationMs) {
		this.durationMs = durationMs;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}


package com.digithink.pos.erp.dto;

public class ErpOperationResult {

	private boolean success;
	private String externalReference;
	private String message;

	public static ErpOperationResult success(String externalReference, String message) {
		ErpOperationResult result = new ErpOperationResult();
		result.setSuccess(true);
		result.setExternalReference(externalReference);
		result.setMessage(message);
		return result;
	}

	public static ErpOperationResult failure(String message) {
		ErpOperationResult result = new ErpOperationResult();
		result.setSuccess(false);
		result.setMessage(message);
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}


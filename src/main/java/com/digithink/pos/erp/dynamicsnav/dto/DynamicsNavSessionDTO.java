package com.digithink.pos.erp.dynamicsnav.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicsNavSessionDTO {

	@JsonProperty("fence_no")
	private String fenceNo;

	@JsonProperty("location")
	private String location;

	@JsonProperty("nber_ticket")
	private Integer nberTicket;

	@JsonProperty("closing_amount")
	private Double closingAmount;

	@JsonProperty("nber_return_cashed")
	private Integer nberReturnCashed;

	@JsonProperty("amount_return_cashed")
	private Double amountReturnCashed;

	@JsonProperty("nber_return")
	private Integer nberReturn;

	// Getters and setters
	public String getFenceNo() {
		return fenceNo;
	}

	public void setFenceNo(String fenceNo) {
		this.fenceNo = fenceNo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getNberTicket() {
		return nberTicket;
	}

	public void setNberTicket(Integer nberTicket) {
		this.nberTicket = nberTicket;
	}

	public Double getClosingAmount() {
		return closingAmount;
	}

	public void setClosingAmount(Double closingAmount) {
		this.closingAmount = closingAmount;
	}

	public Integer getNberReturnCashed() {
		return nberReturnCashed;
	}

	public void setNberReturnCashed(Integer nberReturnCashed) {
		this.nberReturnCashed = nberReturnCashed;
	}

	public Double getAmountReturnCashed() {
		return amountReturnCashed;
	}

	public void setAmountReturnCashed(Double amountReturnCashed) {
		this.amountReturnCashed = amountReturnCashed;
	}

	public Integer getNberReturn() {
		return nberReturn;
	}

	public void setNberReturn(Integer nberReturn) {
		this.nberReturn = nberReturn;
	}
}


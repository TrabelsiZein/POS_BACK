package com.digithink.pos.erp.dynamicsnav.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DynamicsNavCustomerDTO {

	@JsonProperty("No")
	private String number;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("Address_2")
	private String address2;

	@JsonProperty("City")
	private String city;

	@JsonProperty("Post_Code")
	private String postCode;

	@JsonProperty("Country_Region_Code")
	private String countryRegionCode;

	@JsonProperty("Phone_No")
	private String phoneNumber;

	@JsonProperty("E_Mail")
	private String email;

	@JsonProperty("VAT_Registration_No")
	private String vatRegistrationNumber;

	@JsonProperty("Blocked")
	private String blocked;

	@JsonProperty("Balance")
	private BigDecimal balance;

	@JsonProperty("Last_Date_Modified")
	private OffsetDateTime lastModifiedAt;

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getCountryRegionCode() {
		return countryRegionCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getVatRegistrationNumber() {
		return vatRegistrationNumber;
	}

	public String getBlocked() {
		return blocked;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public OffsetDateTime getLastModifiedAt() {
		return lastModifiedAt;
	}
}



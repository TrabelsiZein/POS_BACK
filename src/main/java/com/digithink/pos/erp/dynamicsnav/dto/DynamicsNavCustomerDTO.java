package com.digithink.pos.erp.dynamicsnav.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamicsNavCustomerDTO {

	@JsonProperty("No")
	private String number;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("Phone_No")
	private String phoneNumber;

	@JsonProperty("E_Mail")
	private String email;

	@JsonProperty("Last_Date_Modified")
	private OffsetDateTime lastModifiedAt;
}

package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Company extends _BaseSysEntity {

	@Column(nullable = false, unique = true)
	private String name;

	private String name2;

	private String address;

	private String address2;

	private String email;

	private String email2;

	private String postalCode;

	private String city;

	private String phone;

	private String phone2;

	private String faxNumber;

	private String taxIdentificationNumber;

	private String commercialRegister;

	private String legalStatus;

	private Double capital;

	private String manager;

	private String image;

	private String visa;

}

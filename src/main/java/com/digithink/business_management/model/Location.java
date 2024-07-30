package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Location extends _BaseEntity {

	@Column(length = 20, nullable = false, unique = true)
	private String no;
	@Column(length = 100, nullable = false, unique = true)
	private String description;
	@Column(length = 100)
	private String address;
	@Column(length = 30)
	private String city;
	@Column(length = 20)
	private String phoneNo;
	@Column(length = 100)
	private String contact;
	// ..... Entity
	@Column(length = 20)
	private String postalCode;
	@Column(length = 80)
	private String email;
	@Column(length = 100)
	private String homePage;
	private Boolean useAsInTransit;
	@Column(length = 20)
	private String vATRegistrationNo;

	// FlowFields

	@Transient
	private Double inventory;

}

package com.digithink.business_management.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SeriesLine extends _BaseEntity {

	@Column(length = 20)
	private String documentNo;
	private LocalDate startDate;
	@Column(length = 20)
	private String startNo;
	@Column(length = 20)
	private String endNo;
	@Column(length = 20)
	private String lastNoUsed;
	private LocalDate lastDateUsed;
}

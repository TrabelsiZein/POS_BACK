package com.digithink.base_app.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Currency extends _BaseEntity {

	@Column(nullable = false, unique = true, length = 20)
	private String no;
	@Column(length = 100, nullable = false, unique = true)
	private String description;

}

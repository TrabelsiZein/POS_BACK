package com.digithink.business_management.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class _BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "dd-MM-yyyy-HH:mm:ss")
	protected LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "dd-MM-yyyy-HH:mm:ss")
	protected LocalDateTime updatedAt;

	@Column(name = "created_by", updatable = false)
	protected String createdBy;

	protected String updatedBy;

}

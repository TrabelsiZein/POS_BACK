package com.digithink.business_management.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Role extends _BaseSysEntity {

	@Column(unique = true, nullable = false)
	private String name;

	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();

	public Role(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Role(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
			String name, String description) {
		this.id = id;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.name = name;
		this.description = description;
	}

}
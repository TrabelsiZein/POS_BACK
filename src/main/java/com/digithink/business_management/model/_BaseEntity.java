package com.digithink.business_management.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class _BaseEntity extends _BaseSysEntity {

	protected Long company;

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Company;
import com.digithink.business_management.repository.CompanyRepository;

public class CompanyService extends _BaseService<Company, Long> {

	@Autowired
	private CompanyRepository companyRepository;

	@Override
	protected JpaRepository<Company, Long> getRepository() {
		return companyRepository;
	}

}

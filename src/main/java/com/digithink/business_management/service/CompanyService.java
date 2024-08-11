package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.Company;
import com.digithink.business_management.repository.CompanyRepository;
import com.digithink.business_management.repository._BaseSysRepository;

@Service
public class CompanyService extends _BaseSysService<Company, Long> {

	@Autowired
	private CompanyRepository companyRepository;

	@Override
	protected _BaseSysRepository<Company, Long> getRepository() {
		return companyRepository;
	}

}

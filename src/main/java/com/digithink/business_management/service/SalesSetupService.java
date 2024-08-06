package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.SalesSetup;
import com.digithink.business_management.repository.SalesSetupRepository;

@Service
public class SalesSetupService extends _BaseService<SalesSetup, Long> {

	@Autowired
	private SalesSetupRepository salesSetupRepository;

	@Override
	protected JpaRepository<SalesSetup, Long> getRepository() {
		return salesSetupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<SalesSetup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralLedgerSetup;
import com.digithink.business_management.repository.GeneralLedgerSetupRepository;

@Service
public class GeneralLedgerSetupService extends _BaseService<GeneralLedgerSetup, Long> {

	@Autowired
	private GeneralLedgerSetupRepository generalLedgerSetupRepository;

	@Override
	protected JpaRepository<GeneralLedgerSetup, Long> getRepository() {
		return generalLedgerSetupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<GeneralLedgerSetup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

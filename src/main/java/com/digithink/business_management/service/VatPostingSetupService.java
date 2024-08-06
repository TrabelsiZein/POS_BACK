package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.VatPostingSetup;
import com.digithink.business_management.repository.VatPostingSetupRepository;

@Service
public class VatPostingSetupService extends _BaseService<VatPostingSetup, Long> {

	@Autowired
	private VatPostingSetupRepository vatPostingSetupRepository;

	@Override
	protected JpaRepository<VatPostingSetup, Long> getRepository() {
		return vatPostingSetupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<VatPostingSetup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

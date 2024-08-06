package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.PurchaseSetup;
import com.digithink.business_management.repository.PurchaseSetupRepository;

@Service
public class PurchaseSetupService extends _BaseService<PurchaseSetup, Long> {

	@Autowired
	private PurchaseSetupRepository purchaseSetupRepository;

	@Override
	protected JpaRepository<PurchaseSetup, Long> getRepository() {
		return purchaseSetupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<PurchaseSetup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.setup.PurchaseSetup;
import com.digithink.business_management.repository.PurchaseSetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class PurchaseSetupService extends _BaseService<PurchaseSetup, Long> {

	@Autowired
	private PurchaseSetupRepository purchaseSetupRepository;

	@Override
	protected _BaseRepository<PurchaseSetup, Long> getRepository() {
		return purchaseSetupRepository;
	}

}

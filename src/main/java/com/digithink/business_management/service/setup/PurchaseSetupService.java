package com.digithink.business_management.service.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.setup.PurchaseSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.setup.PurchaseSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class PurchaseSetupService extends _BaseService<PurchaseSetup, Long> {

	@Autowired
	private PurchaseSetupRepository purchaseSetupRepository;

	@Override
	protected _BaseRepository<PurchaseSetup, Long> getRepository() {
		return purchaseSetupRepository;
	}

}

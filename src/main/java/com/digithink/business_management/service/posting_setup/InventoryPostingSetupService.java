package com.digithink.business_management.service.posting_setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_setup.InventoryPostingSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_setup.InventoryPostingSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class InventoryPostingSetupService extends _BaseService<InventoryPostingSetup, Long> {

	@Autowired
	private InventoryPostingSetupRepository inventoryPostingSetupRepository;

	@Override
	protected _BaseRepository<InventoryPostingSetup, Long> getRepository() {
		return inventoryPostingSetupRepository;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.InventoryPostingSetup;
import com.digithink.business_management.repository.InventoryPostingSetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class InventoryPostingSetupService extends _BaseService<InventoryPostingSetup, Long> {

	@Autowired
	private InventoryPostingSetupRepository inventoryPostingSetupRepository;

	@Override
	protected _BaseRepository<InventoryPostingSetup, Long> getRepository() {
		return inventoryPostingSetupRepository;
	}

}

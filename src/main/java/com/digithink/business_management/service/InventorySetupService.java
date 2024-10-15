package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.InventorySetup;
import com.digithink.business_management.repository.InventorySetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class InventorySetupService extends _BaseService<InventorySetup, Long> {

	@Autowired
	private InventorySetupRepository inventorySetupRepository;

	@Override
	protected _BaseRepository<InventorySetup, Long> getRepository() {
		return inventorySetupRepository;
	}

}

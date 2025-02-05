package com.digithink.base_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.base_app.model.InventorySetup;
import com.digithink.base_app.repository.InventorySetupRepository;
import com.digithink.base_app.repository._BaseRepository;

@Service
public class InventorySetupService extends _BaseService<InventorySetup, Long> {

	@Autowired
	private InventorySetupRepository inventorySetupRepository;

	@Override
	protected _BaseRepository<InventorySetup, Long> getRepository() {
		return inventorySetupRepository;
	}

}

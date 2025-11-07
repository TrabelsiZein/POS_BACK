package com.digithink.vacation_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.InventorySetup;
import com.digithink.vacation_app.repository.InventorySetupRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class InventorySetupService extends _BaseService<InventorySetup, Long> {

	@Autowired
	private InventorySetupRepository inventorySetupRepository;

	@Override
	protected _BaseRepository<InventorySetup, Long> getRepository() {
		return inventorySetupRepository;
	}

}

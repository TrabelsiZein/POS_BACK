package com.digithink.business_management.service.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.setup.InventorySetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.setup.InventorySetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class InventorySetupService extends _BaseService<InventorySetup, Long> {

	@Autowired
	private InventorySetupRepository inventorySetupRepository;

	@Override
	protected _BaseRepository<InventorySetup, Long> getRepository() {
		return inventorySetupRepository;
	}

}

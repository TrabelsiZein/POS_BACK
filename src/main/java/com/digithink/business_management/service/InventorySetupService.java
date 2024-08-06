package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.InventorySetup;
import com.digithink.business_management.repository.InventorySetupRepository;

@Service
public class InventorySetupService extends _BaseService<InventorySetup, Long> {

	@Autowired
	private InventorySetupRepository inventorySetupRepository;

	@Override
	protected JpaRepository<InventorySetup, Long> getRepository() {
		return inventorySetupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<InventorySetup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

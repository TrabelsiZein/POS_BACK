package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.InventoryPostingGroup;
import com.digithink.business_management.repository.InventoryPostingGroupRepository;

@Service
public class InventoryPostingGroupService extends _BaseService<InventoryPostingGroup, Long> {

	@Autowired
	private InventoryPostingGroupRepository inventoryPostingGroupRepository;

	@Override
	protected JpaRepository<InventoryPostingGroup, Long> getRepository() {
		return inventoryPostingGroupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<InventoryPostingGroup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.InventoryPostingGroup;
import com.digithink.business_management.repository.InventoryPostingGroupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class InventoryPostingGroupService extends _BaseService<InventoryPostingGroup, Long> {

	@Autowired
	private InventoryPostingGroupRepository inventoryPostingGroupRepository;

	@Override
	protected _BaseRepository<InventoryPostingGroup, Long> getRepository() {
		return inventoryPostingGroupRepository;
	}

}

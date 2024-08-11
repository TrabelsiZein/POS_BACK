package com.digithink.business_management.service.posting_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.InventoryPostingGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_group.InventoryPostingGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class InventoryPostingGroupService extends _BaseService<InventoryPostingGroup, Long> {

	@Autowired
	private InventoryPostingGroupRepository inventoryPostingGroupRepository;

	@Override
	protected _BaseRepository<InventoryPostingGroup, Long> getRepository() {
		return inventoryPostingGroupRepository;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.VatBusinessPostingGroup;
import com.digithink.business_management.repository.VatBusinessPostingGroupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class VatBusinessPostingGroupService extends _BaseService<VatBusinessPostingGroup, Long> {

	@Autowired
	private VatBusinessPostingGroupRepository vatBusinessPostingGroupRepository;

	@Override
	protected _BaseRepository<VatBusinessPostingGroup, Long> getRepository() {
		return vatBusinessPostingGroupRepository;
	}

}

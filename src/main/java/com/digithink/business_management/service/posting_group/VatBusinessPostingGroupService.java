package com.digithink.business_management.service.posting_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.VatBusinessPostingGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_group.VatBusinessPostingGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class VatBusinessPostingGroupService extends _BaseService<VatBusinessPostingGroup, Long> {

	@Autowired
	private VatBusinessPostingGroupRepository vatBusinessPostingGroupRepository;

	@Override
	protected _BaseRepository<VatBusinessPostingGroup, Long> getRepository() {
		return vatBusinessPostingGroupRepository;
	}

}

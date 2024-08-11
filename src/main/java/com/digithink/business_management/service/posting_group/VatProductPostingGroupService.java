package com.digithink.business_management.service.posting_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.VatProductPostingGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_group.VatProductPostingGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class VatProductPostingGroupService extends _BaseService<VatProductPostingGroup, Long> {

	@Autowired
	private VatProductPostingGroupRepository vatProductPostingGroupRepository;

	@Override
	protected _BaseRepository<VatProductPostingGroup, Long> getRepository() {
		return vatProductPostingGroupRepository;
	}

}

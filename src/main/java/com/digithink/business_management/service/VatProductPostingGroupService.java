package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.VatProductPostingGroup;
import com.digithink.business_management.repository.VatProductPostingGroupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class VatProductPostingGroupService extends _BaseService<VatProductPostingGroup, Long> {

	@Autowired
	private VatProductPostingGroupRepository vatProductPostingGroupRepository;

	@Override
	protected _BaseRepository<VatProductPostingGroup, Long> getRepository() {
		return vatProductPostingGroupRepository;
	}

}

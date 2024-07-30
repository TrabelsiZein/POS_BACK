package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.VatProductPostingGroup;
import com.digithink.business_management.repository.VatProductPostingGroupRepository;

@Service
public class VatProductPostingGroupService extends _BaseService<VatProductPostingGroup, Long> {

	@Autowired
	private VatProductPostingGroupRepository vatProductPostingGroupRepository;

	@Override
	protected JpaRepository<VatProductPostingGroup, Long> getRepository() {
		return vatProductPostingGroupRepository;
	}

}

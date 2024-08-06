package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.VatBusinessPostingGroup;
import com.digithink.business_management.repository.VatBusinessPostingGroupRepository;

@Service
public class VatBusinessPostingGroupService extends _BaseService<VatBusinessPostingGroup, Long> {

	@Autowired
	private VatBusinessPostingGroupRepository vatBusinessPostingGroupRepository;

	@Override
	protected JpaRepository<VatBusinessPostingGroup, Long> getRepository() {
		return vatBusinessPostingGroupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<VatBusinessPostingGroup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

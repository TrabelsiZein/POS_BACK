package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralBusinessPostingGroup;
import com.digithink.business_management.repository.GeneralBusinessPostingGroupRepository;

@Service
public class GeneralBusinessPostingGroupService extends _BaseService<GeneralBusinessPostingGroup, Long> {

	@Autowired
	private GeneralBusinessPostingGroupRepository generalBusinessPostingGroupRepository;

	@Override
	protected JpaRepository<GeneralBusinessPostingGroup, Long> getRepository() {
		return generalBusinessPostingGroupRepository;
	}

	@Override
	protected JpaSpecificationExecutor<GeneralBusinessPostingGroup> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

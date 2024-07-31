package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralProductPostingGroup;
import com.digithink.business_management.repository.GeneralProductPostingGroupRepository;

@Service
public class GeneralProductPostingGroupService extends _BaseService<GeneralProductPostingGroup, Long> {

	@Autowired
	private GeneralProductPostingGroupRepository generalProductPostingGroupRepository;

	@Override
	protected JpaRepository<GeneralProductPostingGroup, Long> getRepository() {
		return generalProductPostingGroupRepository;
	}

}

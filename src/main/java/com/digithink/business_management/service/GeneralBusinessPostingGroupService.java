package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralBusinessPostingGroup;
import com.digithink.business_management.repository.GeneralBusinessPostingGroupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class GeneralBusinessPostingGroupService extends _BaseService<GeneralBusinessPostingGroup, Long> {

	@Autowired
	private GeneralBusinessPostingGroupRepository generalBusinessPostingGroupRepository;

	@Override
	protected _BaseRepository<GeneralBusinessPostingGroup, Long> getRepository() {
		return generalBusinessPostingGroupRepository;
	}

}

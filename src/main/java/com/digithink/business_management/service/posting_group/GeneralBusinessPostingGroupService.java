package com.digithink.business_management.service.posting_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.GeneralBusinessPostingGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_group.GeneralBusinessPostingGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class GeneralBusinessPostingGroupService extends _BaseService<GeneralBusinessPostingGroup, Long> {

	@Autowired
	private GeneralBusinessPostingGroupRepository generalBusinessPostingGroupRepository;

	@Override
	protected _BaseRepository<GeneralBusinessPostingGroup, Long> getRepository() {
		return generalBusinessPostingGroupRepository;
	}

}

package com.digithink.business_management.service.posting_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_group.GeneralProductPostingGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_group.GeneralProductPostingGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class GeneralProductPostingGroupService extends _BaseService<GeneralProductPostingGroup, Long> {

	@Autowired
	private GeneralProductPostingGroupRepository generalProductPostingGroupRepository;

	@Override
	protected _BaseRepository<GeneralProductPostingGroup, Long> getRepository() {
		return generalProductPostingGroupRepository;
	}

}

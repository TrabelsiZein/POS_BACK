package com.digithink.business_management.service.posting_setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_setup.GeneralPostingSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_setup.GeneralPostingSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class GeneralPostingSetupService extends _BaseService<GeneralPostingSetup, Long> {

	@Autowired
	GeneralPostingSetupRepository generalPostingSetupRepository;

	@Override
	protected _BaseRepository<GeneralPostingSetup, Long> getRepository() {
		return generalPostingSetupRepository;
	}

}

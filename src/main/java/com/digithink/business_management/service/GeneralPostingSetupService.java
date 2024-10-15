package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralPostingSetup;
import com.digithink.business_management.repository.GeneralPostingSetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class GeneralPostingSetupService extends _BaseService<GeneralPostingSetup, Long> {

	@Autowired
	GeneralPostingSetupRepository generalPostingSetupRepository;

	@Override
	protected _BaseRepository<GeneralPostingSetup, Long> getRepository() {
		return generalPostingSetupRepository;
	}

}

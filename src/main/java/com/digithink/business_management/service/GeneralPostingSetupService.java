package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralPostingSetup;
import com.digithink.business_management.repository.GeneralPostingSetupRepository;

@Service
public class GeneralPostingSetupService extends _BaseService<GeneralPostingSetup, Long> {

	@Autowired
	GeneralPostingSetupRepository generalPostingSetupRepository;

	@Override
	protected JpaRepository<GeneralPostingSetup, Long> getRepository() {
		return generalPostingSetupRepository;
	}

}

package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.VatPostingSetup;
import com.digithink.business_management.repository.VatPostingSetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class VatPostingSetupService extends _BaseService<VatPostingSetup, Long> {

	@Autowired
	private VatPostingSetupRepository vatPostingSetupRepository;

	@Override
	protected _BaseRepository<VatPostingSetup, Long> getRepository() {
		return vatPostingSetupRepository;
	}

}

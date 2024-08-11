package com.digithink.business_management.service.posting_setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.posting_setup.VatPostingSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.posting_setup.VatPostingSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class VatPostingSetupService extends _BaseService<VatPostingSetup, Long> {

	@Autowired
	private VatPostingSetupRepository vatPostingSetupRepository;

	@Override
	protected _BaseRepository<VatPostingSetup, Long> getRepository() {
		return vatPostingSetupRepository;
	}

}

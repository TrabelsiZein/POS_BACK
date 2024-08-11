package com.digithink.business_management.service.general_ledger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.general_ledger.GeneralLedgerSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.general_ledger.GeneralLedgerSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class GeneralLedgerSetupService extends _BaseService<GeneralLedgerSetup, Long> {

	@Autowired
	private GeneralLedgerSetupRepository generalLedgerSetupRepository;

	@Override
	protected _BaseRepository<GeneralLedgerSetup, Long> getRepository() {
		return generalLedgerSetupRepository;
	}

}

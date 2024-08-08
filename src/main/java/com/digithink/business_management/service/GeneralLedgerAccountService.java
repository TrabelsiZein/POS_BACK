package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.general_ledger.GeneralLedgerAccount;
import com.digithink.business_management.repository.GeneralLedgerAccountRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class GeneralLedgerAccountService extends _BaseService<GeneralLedgerAccount, Long> {

	@Autowired
	private GeneralLedgerAccountRepository generalLedgerAccountRepository;

	@Override
	protected _BaseRepository<GeneralLedgerAccount, Long> getRepository() {
		return generalLedgerAccountRepository;
	}

}

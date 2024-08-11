package com.digithink.business_management.service.general_ledger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.general_ledger.GeneralLedgerAccount;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.general_ledger.GeneralLedgerAccountRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class GeneralLedgerAccountService extends _BaseService<GeneralLedgerAccount, Long> {

	@Autowired
	private GeneralLedgerAccountRepository generalLedgerAccountRepository;

	@Override
	protected _BaseRepository<GeneralLedgerAccount, Long> getRepository() {
		return generalLedgerAccountRepository;
	}

}

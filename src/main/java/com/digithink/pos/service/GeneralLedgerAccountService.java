package com.digithink.vacation_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.GeneralLedgerAccount;
import com.digithink.vacation_app.repository.GeneralLedgerAccountRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class GeneralLedgerAccountService extends _BaseService<GeneralLedgerAccount, Long> {

	@Autowired
	private GeneralLedgerAccountRepository generalLedgerAccountRepository;

	@Override
	protected _BaseRepository<GeneralLedgerAccount, Long> getRepository() {
		return generalLedgerAccountRepository;
	}

}

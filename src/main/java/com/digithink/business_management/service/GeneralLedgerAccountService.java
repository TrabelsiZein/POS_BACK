package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.GeneralLedgerAccount;
import com.digithink.business_management.repository.GeneralLedgerAccountRepository;

@Service
public class GeneralLedgerAccountService extends _BaseService<GeneralLedgerAccount, Long> {

	@Autowired
	private GeneralLedgerAccountRepository generalLedgerAccountRepository;

	@Override
	protected JpaRepository<GeneralLedgerAccount, Long> getRepository() {
		return generalLedgerAccountRepository;
	}

	@Override
	protected JpaSpecificationExecutor<GeneralLedgerAccount> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}

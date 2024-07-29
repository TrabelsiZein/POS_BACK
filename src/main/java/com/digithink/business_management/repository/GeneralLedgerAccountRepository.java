package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.GeneralLedgerAccount;

public interface GeneralLedgerAccountRepository extends JpaRepository<GeneralLedgerAccount, Long> {

}

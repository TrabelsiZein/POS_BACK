package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.GeneralLedgerAccount;
import com.digithink.business_management.service.GeneralLedgerAccountService;

@RestController
@RequestMapping("general_ledger_account")
public class GeneralLedgerAccountAPI extends _BaseController<GeneralLedgerAccount, Long, GeneralLedgerAccountService> {

}

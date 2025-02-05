package com.digithink.base_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.base_app.model.GeneralLedgerAccount;
import com.digithink.base_app.service.GeneralLedgerAccountService;

@RestController
@RequestMapping("general_ledger_account")
public class GeneralLedgerAccountAPI extends _BaseController<GeneralLedgerAccount, Long, GeneralLedgerAccountService> {

}

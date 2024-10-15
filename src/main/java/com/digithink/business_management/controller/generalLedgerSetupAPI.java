package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.GeneralLedgerSetup;
import com.digithink.business_management.service.GeneralLedgerSetupService;

@RestController
@RequestMapping("general_ledger_setup")
public class generalLedgerSetupAPI extends _BaseController<GeneralLedgerSetup, Long, GeneralLedgerSetupService> {

}

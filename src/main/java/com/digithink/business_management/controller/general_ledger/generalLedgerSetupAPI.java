package com.digithink.business_management.controller.general_ledger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.general_ledger.GeneralLedgerSetup;
import com.digithink.business_management.service.general_ledger.GeneralLedgerSetupService;

@RestController
@RequestMapping("general_ledger_setup")
public class generalLedgerSetupAPI extends _BaseController<GeneralLedgerSetup, Long, GeneralLedgerSetupService> {

}

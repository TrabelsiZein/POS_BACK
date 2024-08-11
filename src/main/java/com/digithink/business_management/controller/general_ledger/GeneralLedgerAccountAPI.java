package com.digithink.business_management.controller.general_ledger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.general_ledger.GeneralLedgerAccount;
import com.digithink.business_management.service.general_ledger.GeneralLedgerAccountService;

@RestController
@RequestMapping("general_ledger_account")
public class GeneralLedgerAccountAPI extends _BaseController<GeneralLedgerAccount, Long, GeneralLedgerAccountService> {

}

package com.digithink.business_management.controller.posting_setup;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.posting_setup.VatPostingSetup;
import com.digithink.business_management.service.posting_setup.VatPostingSetupService;

@RestController
@RequestMapping("vat_posting_setup")
public class VatPostingSetupAPI extends _BaseController<VatPostingSetup, Long, VatPostingSetupService> {

}

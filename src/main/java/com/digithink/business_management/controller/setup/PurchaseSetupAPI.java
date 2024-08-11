package com.digithink.business_management.controller.setup;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.setup.PurchaseSetup;
import com.digithink.business_management.service.setup.PurchaseSetupService;

@RestController
@RequestMapping("purchase_setup")
public class PurchaseSetupAPI extends _BaseController<PurchaseSetup, Long, PurchaseSetupService> {

}

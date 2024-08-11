package com.digithink.business_management.controller.posting_setup;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.posting_setup.InventoryPostingSetup;
import com.digithink.business_management.service.posting_setup.InventoryPostingSetupService;

@RestController
@RequestMapping("inventory_posting_setup")
public class InventoryPostingSetupAPI
		extends _BaseController<InventoryPostingSetup, Long, InventoryPostingSetupService> {

}

package com.digithink.business_management.controller.setup;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.setup.InventorySetup;
import com.digithink.business_management.service.setup.InventorySetupService;

@RestController
@RequestMapping("inventory_setup")
public class InventorySetupAPI extends _BaseController<InventorySetup, Long, InventorySetupService> {

}

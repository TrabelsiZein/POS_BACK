package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.InventorySetup;
import com.digithink.business_management.service.InventorySetupService;

@RequestMapping("inentory_setup")
public class InventorySetupAPI extends _BaseController<InventorySetup, Long, InventorySetupService> {

}

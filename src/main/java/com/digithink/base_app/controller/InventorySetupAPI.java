package com.digithink.base_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.base_app.model.InventorySetup;
import com.digithink.base_app.service.InventorySetupService;

@RestController
@RequestMapping("inventory_setup")
public class InventorySetupAPI extends _BaseController<InventorySetup, Long, InventorySetupService> {

}

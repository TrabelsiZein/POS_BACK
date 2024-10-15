package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.PurchaseSetup;
import com.digithink.business_management.service.PurchaseSetupService;

@RestController
@RequestMapping("purchase_setup")
public class PurchaseSetupAPI extends _BaseController<PurchaseSetup, Long, PurchaseSetupService> {

}

package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.setup.SalesSetup;
import com.digithink.business_management.service.SalesSetupService;

@RestController
@RequestMapping("sales_setup")
public class SalesSetupAPI extends _BaseController<SalesSetup, Long, SalesSetupService> {

}

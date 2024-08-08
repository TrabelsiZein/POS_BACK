package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.system.Company;
import com.digithink.business_management.service.CompanyService;

@RestController
@RequestMapping("company")
public class CompanyAPI extends _BaseController<Company, Long, CompanyService> {

}

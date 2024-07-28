package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.Company;
import com.digithink.business_management.service.CompanyService;

@RequestMapping("company")
public class CompanyAPI extends _BaseController<Company, Long, CompanyService> {

}

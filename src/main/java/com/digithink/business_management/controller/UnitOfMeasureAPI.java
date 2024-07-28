package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.UnitOfMeasure;
import com.digithink.business_management.service.UnitOfMeasureService;

@RestController
@RequestMapping("unit_of_mesure")
public class UnitOfMeasureAPI extends _BaseController<UnitOfMeasure, Long, UnitOfMeasureService> {

}

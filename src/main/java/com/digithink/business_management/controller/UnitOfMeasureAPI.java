package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.UnitOfMeasure;
import com.digithink.business_management.service.UnitOfMeasureService;

@RequestMapping("unit_of_mesure")
public class UnitOfMeasureAPI extends _BaseController<UnitOfMeasure, Long, UnitOfMeasureService> {

}

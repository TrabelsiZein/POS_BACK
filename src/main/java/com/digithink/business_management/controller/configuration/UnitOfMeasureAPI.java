package com.digithink.business_management.controller.configuration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.configuration.UnitOfMeasure;
import com.digithink.business_management.service.configuration.UnitOfMeasureService;

@RestController
@RequestMapping("unit_of_measure")
public class UnitOfMeasureAPI extends _BaseController<UnitOfMeasure, Long, UnitOfMeasureService> {

}

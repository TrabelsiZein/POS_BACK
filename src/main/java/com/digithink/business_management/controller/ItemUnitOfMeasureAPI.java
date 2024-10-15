package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.configuration.ItemUnitOfMeasure;
import com.digithink.business_management.service.configuration.ItemUnitOfMeasureService;

@RestController
@RequestMapping("item_unit_of_measure")
public class ItemUnitOfMeasureAPI extends _BaseController<ItemUnitOfMeasure, Long, ItemUnitOfMeasureService> {

}

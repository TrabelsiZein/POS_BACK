package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.ItemUnitOfMeasure;
import com.digithink.business_management.service.ItemUnitOfMeasureService;

@RestController
@RequestMapping("item_unit_of_measure")
public class ItemUnitOfMeasureAPI extends _BaseController<ItemUnitOfMeasure, Long, ItemUnitOfMeasureService> {

}

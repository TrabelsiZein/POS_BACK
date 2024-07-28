package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.ItemUnitOfMeasure;
import com.digithink.business_management.service.ItemUnitOfMeasureService;

@RequestMapping("item_unit_of_measure")
public class ItemUnitOfMeasureAPI extends _BaseController<ItemUnitOfMeasure, Long, ItemUnitOfMeasureService> {

}

package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.configuration.ItemDiscountGroup;
import com.digithink.business_management.service.configuration.ItemDiscountGroupService;

@RestController
@RequestMapping("item_discount_group")
public class ItemDiscountGroupAPI extends _BaseController<ItemDiscountGroup, Long, ItemDiscountGroupService> {

}

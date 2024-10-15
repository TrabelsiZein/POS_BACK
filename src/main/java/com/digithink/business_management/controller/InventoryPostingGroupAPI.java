package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.InventoryPostingGroup;
import com.digithink.business_management.service.InventoryPostingGroupService;

@RestController
@RequestMapping("inventory_posting_group")
public class InventoryPostingGroupAPI
		extends _BaseController<InventoryPostingGroup, Long, InventoryPostingGroupService> {

}

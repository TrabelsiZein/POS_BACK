package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.InventoryPostingGroup;
import com.digithink.business_management.service.InventoryPostingGroupService;

@RequestMapping("inventory_posting_group")
public class InventoryPostingGroupAPI
		extends _BaseController<InventoryPostingGroup, Long, InventoryPostingGroupService> {

}

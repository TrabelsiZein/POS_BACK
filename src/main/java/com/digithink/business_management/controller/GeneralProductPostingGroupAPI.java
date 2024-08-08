package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.posting_group.GeneralProductPostingGroup;
import com.digithink.business_management.service.GeneralProductPostingGroupService;

@RestController
@RequestMapping("general_product_posting_group")
public class GeneralProductPostingGroupAPI
		extends _BaseController<GeneralProductPostingGroup, Long, GeneralProductPostingGroupService> {

}

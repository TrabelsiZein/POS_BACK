package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.GeneralBusinessPostingGroup;
import com.digithink.business_management.service.GeneralBusinessPostingGroupService;

@RestController
@RequestMapping("general_business_posting_group")
public class GeneralBusinessPostingGroupAPI
		extends _BaseController<GeneralBusinessPostingGroup, Long, GeneralBusinessPostingGroupService> {

}

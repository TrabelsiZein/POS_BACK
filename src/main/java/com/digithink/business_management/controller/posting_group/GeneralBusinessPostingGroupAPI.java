package com.digithink.business_management.controller.posting_group;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.posting_group.GeneralBusinessPostingGroup;
import com.digithink.business_management.service.posting_group.GeneralBusinessPostingGroupService;

@RestController
@RequestMapping("general_business_posting_group")
public class GeneralBusinessPostingGroupAPI
		extends _BaseController<GeneralBusinessPostingGroup, Long, GeneralBusinessPostingGroupService> {

}

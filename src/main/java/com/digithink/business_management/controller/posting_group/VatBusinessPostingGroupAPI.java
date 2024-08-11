package com.digithink.business_management.controller.posting_group;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.posting_group.VatBusinessPostingGroup;
import com.digithink.business_management.service.posting_group.VatBusinessPostingGroupService;

@RestController
@RequestMapping("vat_business_posting_group")
public class VatBusinessPostingGroupAPI
		extends _BaseController<VatBusinessPostingGroup, Long, VatBusinessPostingGroupService> {

}

package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.posting_group.VatProductPostingGroup;
import com.digithink.business_management.service.VatProductPostingGroupService;

@RestController
@RequestMapping("vat_product_posting_group")
public class VatProductPostingGroupAPI
		extends _BaseController<VatProductPostingGroup, Long, VatProductPostingGroupService> {

}

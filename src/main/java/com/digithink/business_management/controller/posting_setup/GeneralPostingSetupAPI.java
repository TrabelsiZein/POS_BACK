package com.digithink.business_management.controller.posting_setup;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.posting_setup.GeneralPostingSetup;
import com.digithink.business_management.service.posting_setup.GeneralPostingSetupService;

@RestController
@RequestMapping("general_posting_setup")
public class GeneralPostingSetupAPI extends _BaseController<GeneralPostingSetup, Long, GeneralPostingSetupService> {

}

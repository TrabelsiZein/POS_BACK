package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.MemberFunction;
import com.digithink.pos.service.MemberFunctionService;

@RestController
@RequestMapping("member-function")
public class MemberFunctionAPI extends _BaseController<MemberFunction, Long, MemberFunctionService> {

}

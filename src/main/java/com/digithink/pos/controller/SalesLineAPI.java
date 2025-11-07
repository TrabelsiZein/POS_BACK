package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.SalesLine;
import com.digithink.pos.service.SalesLineService;

@RestController
@RequestMapping("sales-line")
public class SalesLineAPI extends _BaseController<SalesLine, Long, SalesLineService> {

}


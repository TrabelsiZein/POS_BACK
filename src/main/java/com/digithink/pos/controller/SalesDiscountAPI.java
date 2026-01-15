package com.digithink.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.SalesDiscount;
import com.digithink.pos.service.SalesDiscountService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("sales-discount")
@Log4j2
public class SalesDiscountAPI extends _BaseController<SalesDiscount, Long, SalesDiscountService> {

	@Autowired
	private SalesDiscountService salesDiscountService;
}


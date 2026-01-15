package com.digithink.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.SalesPrice;
import com.digithink.pos.service.SalesPriceService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("sales-price")
@Log4j2
public class SalesPriceAPI extends _BaseController<SalesPrice, Long, SalesPriceService> {

	@Autowired
	private SalesPriceService salesPriceService;
}


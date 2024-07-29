package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.Currency;
import com.digithink.business_management.service.CurrencyService;

@RestController
@RequestMapping("currency")
public class CurrencyAPI extends _BaseController<Currency, Long, CurrencyService> {

}

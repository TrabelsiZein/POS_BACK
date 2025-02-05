package com.digithink.base_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.base_app.model.Currency;
import com.digithink.base_app.service.CurrencyService;

@RestController
@RequestMapping("currency")
public class CurrencyAPI extends _BaseController<Currency, Long, CurrencyService> {

}

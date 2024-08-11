package com.digithink.business_management.controller.configuration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.configuration.Currency;
import com.digithink.business_management.service.configuration.CurrencyService;

@RestController
@RequestMapping("currency")
public class CurrencyAPI extends _BaseController<Currency, Long, CurrencyService> {

}

package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.service.PaymentMethodService;

@RestController
@RequestMapping("payment-method")
public class PaymentMethodAPI extends _BaseController<PaymentMethod, Long, PaymentMethodService> {

}


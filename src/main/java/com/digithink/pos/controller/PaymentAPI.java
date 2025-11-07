package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Payment;
import com.digithink.pos.service.PaymentService;

@RestController
@RequestMapping("payment")
public class PaymentAPI extends _BaseController<Payment, Long, PaymentService> {

}


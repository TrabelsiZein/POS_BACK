package com.digithink.vacation_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.vacation_app.model.UserAccount;
import com.digithink.vacation_app.service.UserAccountService;

@RestController
@RequestMapping("user")
public class UseAccountAPI extends _BaseController<UserAccount, Long, UserAccountService> {

}

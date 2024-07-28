package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.service.UserAccountService;

@RestController
@RequestMapping("user")
public class UserAccountAPI extends _BaseController<UserAccount, Long, UserAccountService> {

//	@Override
//	public List<?> getAll() {
//		// TODO Auto-generated method stub
//		return service.findAll();
//	}

}

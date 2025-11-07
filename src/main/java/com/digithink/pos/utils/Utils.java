package com.digithink.pos.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.digithink.pos.model.UserAccount;
import com.digithink.pos.service.UserAccountService;

@Component
public class Utils {

	@Autowired
	private UserAccountService accountService;

	public String getUsername() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		return authentication.getName();
	}

	public UserAccount getCurrentUserAccount() {
		return accountService.findByUsername(getUsername()).get();
	}

}

package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.Role;
import com.digithink.business_management.service.RoleService;

@RequestMapping("role")
public class RoleAPI extends _BaseController<Role, Long, RoleService> {

}

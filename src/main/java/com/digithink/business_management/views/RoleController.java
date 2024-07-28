package com.digithink.business_management.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.Role;
import com.digithink.business_management.service.RoleService;

@Controller
@RequestMapping("/thymeleaf/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping("/list")
	public String listRoles(Model model) {
		model.addAttribute("roles", roleService.findAll());
		return "role/list";
	}

	@GetMapping("/add")
	public String addRole(Model model) {
		model.addAttribute("role", new Role());
		return "role/form";
	}

	@GetMapping("/edit/{id}")
	public String editRole(@PathVariable("id") Long id, Model model) {
		model.addAttribute("role", roleService.findById(id).orElse(new Role()));
		return "role/form";
	}

	@PostMapping("/save")
	public String saveRole(@ModelAttribute Role role) {
		roleService.save(role);
		return "redirect:/role/list";
	}

	@GetMapping("/delete/{id}")
	public String deleteRole(@PathVariable("id") Long id) {
		roleService.deleteById(id);
		return "redirect:/role/list";
	}
}

package com.digithink.business_management.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.Permission;
import com.digithink.business_management.service.PermissionService;

@Controller
@RequestMapping("/thymeleaf/permission")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@GetMapping("/list")
	public String listPermissions(Model model) {
		model.addAttribute("permissions", permissionService.findAll());
		return "permission/list";
	}

	@GetMapping("/add")
	public String addPermission(Model model) {
		model.addAttribute("permission", new Permission());
		return "permission/form";
	}

	@GetMapping("/edit/{id}")
	public String editPermission(@PathVariable("id") Long id, Model model) {
		model.addAttribute("permission", permissionService.findById(id).orElse(new Permission()));
		return "permission/form";
	}

	@PostMapping("/save")
	public String savePermission(@ModelAttribute Permission permission) {
		permissionService.save(permission);
		return "redirect:/permission/list";
	}

	@GetMapping("/delete/{id}")
	public String deletePermission(@PathVariable("id") Long id) {
		permissionService.deleteById(id);
		return "redirect:/permission/list";
	}
}

package com.digithink.business_management.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.service.UserAccountService;

@Controller
@RequestMapping("/thymeleaf/user")
public class UserController {

	@Autowired
	private UserAccountService userAccountService;

	@GetMapping("/list")
	public String listUsers(Model model) {
		model.addAttribute("users", userAccountService.findAll());
		return "user/list";
	}

	@GetMapping("/add")
	public String addUser(Model model) {
		model.addAttribute("user", new UserAccount());
		return "user/form";
	}

	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", userAccountService.findById(id).orElse(new UserAccount()));
		return "user/form";
	}

	@PostMapping("/save")
	public String saveUser(@ModelAttribute UserAccount user) {
		userAccountService.save(user);
		return "redirect:/user/list";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id) {
		userAccountService.deleteById(id);
		return "redirect:/user/list";
	}
}

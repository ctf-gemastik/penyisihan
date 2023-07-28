package id.co.gemastik.web.webtool.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import id.co.gemastik.web.webtool.model.AccountModel;
import id.co.gemastik.web.webtool.repository.AccountRepository;

@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
	private AccountRepository accountRepo;
	
	@GetMapping("/delete")
	public String deleteAccount(HttpSession session, Model model) {
		if (session.getAttribute("isLogin") == null) return "redirect:/auth/login";
		AccountModel acc = accountRepo.findByUsername((String)session.getAttribute("username"));
		accountRepo.delete(acc);
		session.invalidate();
		
		return "redirect:/";
	}
}

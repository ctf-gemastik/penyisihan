package id.co.gemastik.web.webtool.controller;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.co.gemastik.web.webtool.model.AccountModel;
import id.co.gemastik.web.webtool.repository.AccountRepository;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
	@Autowired
	private AccountRepository accountRepo;

	@Value("${userdata.path}")
	private String userDataPath;
	
	@GetMapping("/login")
	public String loginForm(HttpSession session, Model model) {
		if (session.getAttribute("isLogin") != null) return "redirect:/";
		return "login";
	}
	
	@PostMapping("/login")
	public String login(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			HttpSession session,
			RedirectAttributes redirectAttributes
		) {
		if (session.getAttribute("isLogin") != null) return "redirect:/";
		var acc = accountRepo.findByUsername(username);
		if (acc == null || !acc.validatePassword(password)) {
			redirectAttributes.addFlashAttribute("error_msg", "Username or password is wrong.");
            return "redirect:/auth/login";
		}
		
	    session.setAttribute("isLogin", true);
	    session.setAttribute("username", acc.getUsername());
		return "redirect:/tools";
	}
	
	@GetMapping("/register")
	public String registerForm(HttpSession session, Model model) {
		if (session.getAttribute("isLogin") != null) return "redirect:/";
		return "register";
	}
	
	@PostMapping("/register")
	public String register(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			HttpSession session,
			RedirectAttributes redirectAttributes
		) throws Exception {
		if (session.getAttribute("isLogin") != null) return "redirect:/";
		
		AccountModel existAcc = accountRepo.findByUsername(username);
		if (existAcc != null) {
			redirectAttributes.addFlashAttribute("error_msg", "Username already exist.");
            return "redirect:/auth/register";
		}

		var newAcc = new AccountModel(username, password);
        accountRepo.save(newAcc);
        
        File templateFolder = new File(userDataPath + "/template");
        File destFolder = new File(userDataPath + "/" + newAcc.getFolder());
        FileUtils.copyDirectory(templateFolder, destFolder);
        
        redirectAttributes.addFlashAttribute("success_msg", "User created successfully. You can login.");
		return "redirect:/auth/login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		return "redirect:/auth/login";
	}
}

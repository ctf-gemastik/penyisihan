package id.co.gemastik.web.webtool.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.co.gemastik.web.webtool.repository.AccountRepository;

@Controller
public class ToolingController {
	@Autowired
	AccountRepository accountRepository;
	
	@Value("${userdata.path}")
	private String userDataPath;

	@GetMapping("/tools")
	public String toolsPage(HttpSession session, Model model) {
		if (session.getAttribute("isLogin") == null) return "redirect:/auth/login";
		return "tools";
	}
	
	@PostMapping("/execute")
	public String executePage(
			@RequestParam("program") String programName,
			@RequestParam("file") MultipartFile file,
			HttpSession session,
			RedirectAttributes redirectAttributes
		) throws Exception {
		if (session.getAttribute("isLogin") == null) return "redirect:/auth/login";
		if (!programName.equals("md5sum") && !programName.equals("base64")) {
			redirectAttributes.addFlashAttribute("error_msg", "Invalid program name.");
			return "redirect:/tools";
		}
		
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("error_msg", "Failed to store empty file.");
			return "redirect:/tools";
		}
		
		Path destPath = Path.of("/tmp", UUID.randomUUID().toString());
		InputStream inputStream = file.getInputStream();
		Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
		
		String userFolder = (String)session.getAttribute("username");
		if (accountRepository.findByUsername(userFolder) != null) {
			userFolder = accountRepository.findByUsername(userFolder).getFolder();
		}
		
		String programPath = userDataPath + "/" + userFolder + "/" + programName;
		
		ProcessBuilder processBuilder = new ProcessBuilder(programPath, destPath.toString());
		Process process = processBuilder.start();
		process.waitFor(10, TimeUnit.SECONDS);
		process.destroy();
		
		redirectAttributes.addFlashAttribute("success_msg", "Finish executing.");
		return "redirect:/tools";
	}
}

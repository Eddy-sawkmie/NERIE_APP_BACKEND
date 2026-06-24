package com.nic.nerie.authentication.controller;

import com.nic.nerie.authentication.service.AuthenticationService;
import com.nic.nerie.captcha.service.CaptchaService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthenticationController {
	private final AuthenticationService authenticationService;
	private final CaptchaService captchaService;
	private final MT_UserloginService userloginService;
	private static final Logger logger = LoggerFactory.getLogger("AUTHENTICATION_LOGGER");
	
	@Value("${JWT_EXP_CLIENT}")
	private Long tokenExpirationSeconds;

	@Autowired
	public AuthenticationController(
		AuthenticationService authenticationService, 
		CaptchaService captchaService,
		MT_UserloginService userloginService
	) {
		this.authenticationService = authenticationService;
		this.captchaService = captchaService;
		this.userloginService = userloginService;
	}

	/*
	 *	Renders login page with captcha 
	 */
	@GetMapping("/login")
	public String renderLoginPage(Model model, @RequestParam(value = "msg", required = false) String msg) {
		model.addAttribute("currentPage", "login");
		model.addAttribute("captchaPrincipal", captchaService.generateNewCaptcha());

		if (msg != null && !msg.isBlank())
			model.addAttribute("msg", msg.trim());

		return "pages/landing/login";
	}

	/*
	 * Login endpoint
	 * Verifies user credentials and ensures the user account is enabled.
	 * Returns the JWT token in two formats to support different clients:
	 * 1. As an HttpOnly cookie (handled automatically by Web App clients)
	 * 2. In the JSON response body (for Mobile App or API clients)
	 */
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(@RequestBody MT_Userlogin user) {
		String jwtToken;

		if (user.getUserid() == null || user.getUserpassword() == null ||
				user.getUserid().isBlank() || user.getUserpassword().isBlank())
			return ResponseEntity.badRequest().body("userid and password cannot be null or empty");

		if ((jwtToken = authenticationService.verify(user)) == null) {
			logger.error("Authentication failed for userid {}", user.getUserid());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid userid or password");
		}

		if (!userloginService.getIsUserEnabledByUserid(user.getUserid())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Your account is disabled. Please contact the administrator.");
		}

		// Create Cookie (For Web App)
		ResponseCookie jwtCookie = ResponseCookie.from("neriejwt", jwtToken)
				.httpOnly(true)
				// .secure(true) // Uncomment for Production (HTTPS)
				.sameSite("Strict")
				.path("/")
				.maxAge(tokenExpirationSeconds)
				.build();

		// Create Response Body (For Mobile App)
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("token", jwtToken);
		responseBody.put("userid", user.getUserid());
		responseBody.put("message", "Login successful");

		logger.info("Authentication successful for userid {}", user.getUserid());

		// Return BOTH
		return ResponseEntity.ok()
				.header("Set-Cookie", jwtCookie.toString())
				.body(responseBody);
	}

	/*
	 * ISSUE:
	 * Spring Security reserves /logout by default.
	 * Requests to `/logout` are intercepted by the LogoutFilter,
	 * which prevents controller-level logout handlers from being invoked.
	 *
     * COMMENTED OUT:
	 * Logout handling has been intentionally removed from the controller.
	 *
	 * Logout is now managed by Spring Security’s LogoutFilter via the security configuration
	 * To ensure proper authentication cleanup, reliable JWT cookie deletion
	 * And consistent behavior within a stateless security context.
	 *
	 * Centralizing logout in the security layer prevents filter re-authentication issues

	 =========================================================================================

	 * 	Logout endpoint
	 *	Resets client's JWT token to empty string and sets maxAge to 0
	 *  Cookie is set automatically in the client-side

	@GetMapping("/logout")
	public ResponseEntity<?> logout() {
		MT_Userlogin user = userloginService.getUserloginFromAuthentication();

		ResponseCookie jwtCookie = ResponseCookie.from("neriejwt", "")
				.httpOnly(true)
				.sameSite("Strict")
				.path("/")
				.maxAge(0)
				.build();

		logger.info("Logging-out successful for userid {}", user.getUserid());
		return ResponseEntity.ok().header("Set-Cookie", jwtCookie.toString()).build();
	}
	*/

}
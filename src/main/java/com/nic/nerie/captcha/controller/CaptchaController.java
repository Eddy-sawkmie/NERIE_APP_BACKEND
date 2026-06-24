package com.nic.nerie.captcha.controller;

import com.nic.nerie.captcha.model.CaptchaPrincipal;
import com.nic.nerie.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;

    @Autowired
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/validate-captcha")
    public ResponseEntity<?> validateCaptcha(@RequestParam("captcha") String userInput,
                                             @RequestParam("expected") String expectedCaptcha) {
        if (userInput.equals(expectedCaptcha)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/reload-captcha")
    public ResponseEntity<CaptchaPrincipal> reloadCaptcha() {
        return ResponseEntity.ok(captchaService.generateNewCaptcha());
    }
}

package com.nic.nerie.error.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/401")
    public String render401ErrorPage(@RequestParam(value = "msg", required = false) String msg, Model model) {
        model.addAttribute("msg", msg != null ? msg.trim() : null);
        return "pages/error/401";
    }

    @GetMapping("/403")
    public String render403ErrorPage(@RequestParam(value = "msg", required = false) String msg, Model model) {
        model.addAttribute("msg", msg != null ? msg.trim() : null);
        return "pages/error/403";
    }

    @GetMapping("/404")
    public String render404ErrorPage(@RequestParam(value = "msg", required = false) String msg, Model model) {
        model.addAttribute("msg", msg != null ? msg.trim() : null);
        return "pages/error/404";
    }

    @GetMapping("/500")
    public String render500ErrorPage(@RequestParam(value = "msg", required = false) String msg, Model model) {
        model.addAttribute("msg", msg != null ? msg.trim() : null);
        return "pages/error/500";
    }
}

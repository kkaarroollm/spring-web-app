package com.test.demo.controller;

import com.test.demo.model.User;
import com.test.demo.service.auth.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class.getName());

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, HttpServletRequest request, Model model) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }

        if (error == null) {
            model.addAttribute("pageTitle", "Login");
            return "login";
        }

        String errorMessage = Optional.ofNullable(request.getSession(false))
                .map(session -> (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION))
                .map(AuthenticationException::getMessage)
                .orElse("Unknown error");

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user){
        logger.info("User: " + user);
        userService.saveUser(user);
        return "login";
    }

//    @RequestMapping("/default")
//    public String defaultAfterLogin(HttpServletRequest request) {
//        if (request.isUserInRole("ROLE_ADMIN")) {
//            return "redirect:/admin";
//        }
//        return "redirect:/profile";
//    }


    /* endpoint for email verification and handling this service */
}

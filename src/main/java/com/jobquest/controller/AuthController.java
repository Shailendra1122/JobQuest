package com.jobquest.controller;

import com.jobquest.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for login and registration pages.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** GET /login — Show the login page. */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Account created! Please log in.");
        }
        return "login";
    }

    /** GET /register — Show the registration page. */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /** POST /register — Handle registration form submission. */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String displayName,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Validate inputs
        if (username.length() < 3) {
            model.addAttribute("errorMessage", "Username must be at least 3 characters");
            model.addAttribute("username", username);
            model.addAttribute("displayName", displayName);
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password must be at least 6 characters");
            model.addAttribute("username", username);
            model.addAttribute("displayName", displayName);
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            model.addAttribute("username", username);
            model.addAttribute("displayName", displayName);
            return "register";
        }

        try {
            userService.register(username, password, displayName);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully!");
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("displayName", displayName);
            return "register";
        }
    }
}

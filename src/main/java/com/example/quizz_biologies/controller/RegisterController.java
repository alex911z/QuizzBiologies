package com.example.quizz_biologies.controller;

import com.example.quizz_biologies.model.AppUser;
import com.example.quizz_biologies.model.Role;
import com.example.quizz_biologies.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.teacher.invite-code:teach123}")
    private String teacherInviteCode;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerPage(@RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "register";
    }

    @PostMapping("/student")
    public String registerStudent(@RequestParam String username, @RequestParam String password) {
        if (userRepository.existsByUsername(username)) {
            return "redirect:/register?error=Username+already+exists";
        }
        createUser(username, password, Role.STUDENT);
        return "redirect:/login";
    }

    @PostMapping("/teacher")
    public String registerTeacher(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam("inviteCode") String inviteCode) {
        if (teacherInviteCode != null && !teacherInviteCode.isBlank() && !teacherInviteCode.equals(inviteCode)) {
            return "redirect:/register?error=Invalid+teacher+invite+code";
        }
        if (userRepository.existsByUsername(username)) {
            return "redirect:/register?error=Username+already+exists";
        }
        createUser(username, password, Role.TEACHER);
        return "redirect:/login";
    }

    private void createUser(String username, String password, Role role) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password)); // BCrypt
        u.setRole(role);
        userRepository.save(u);
    }
}

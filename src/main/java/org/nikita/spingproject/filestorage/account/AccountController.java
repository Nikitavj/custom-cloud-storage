package org.nikita.spingproject.filestorage.account;

import io.minio.errors.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.account.exception.UserAlreadyExistException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AccountController {
    private final UserServiceImpl userService;

    @GetMapping("/log-up")
    public String showRegistrationForm(@ModelAttribute("user") UserDto user) {
        return "logup";
    }

    @PostMapping("/log-up")
    public String registerUserAccount(HttpServletRequest request,
                                      @ModelAttribute("user") @Valid UserDto userDto,
                                      BindingResult bindingResult) {

        try {
            userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException e) {
            bindingResult.rejectValue("email", "", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            return "logup";
        }

        try {
            request.login(userDto.getEmail(), userDto.getPassword());
        } catch (ServletException e) {
            return "login";
        }

        return "redirect:/";
    }

    @GetMapping("/log-in")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/log-in")
    public String errorLoginForm() {
        return "login";
    }
}

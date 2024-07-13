package org.nikita.spingproject.filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.nikita.spingproject.filestorage.service.UserAlreadyExistException;
import org.nikita.spingproject.filestorage.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AccountController {
    private final UserServiceImpl userService;

    @GetMapping("/log-up")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "logup";
    }

    @PostMapping("/log-up")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                      BindingResult bindingResult) {

        try {
            userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException e) {
            bindingResult.rejectValue("email", "", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            return "logup";
        }

        return "redirect:/";
    }

    @GetMapping("/log-in")
    public String showLoginForm() {
        return "login";
    }
}

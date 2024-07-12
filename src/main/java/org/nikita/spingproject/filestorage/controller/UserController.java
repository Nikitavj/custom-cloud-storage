package org.nikita.spingproject.filestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.nikita.spingproject.filestorage.service.UserServiceImpl;
import org.nikita.spingproject.filestorage.utils.UserValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserValidator userValidator;

    @GetMapping
    public String userPage(Model model) {
        return "user";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                      BindingResult bindingResult) {
        userValidator.validate(userDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.registerNewUserAccount(userDto);
        return "redirect:user";
    }
}

package org.nikita.spingproject.filestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class FilesController {

    @GetMapping
    public String index() {
        return "home";
    }

    @GetMapping("/search")
    public String showSearchform() {
        return "search";
    }

    @PostMapping("/search")
    public String searchFile() {
        return "search";
    }
}

package org.nikita.spingproject.filestorage.controller;

import org.nikita.spingproject.filestorage.model.User;
import org.nikita.spingproject.filestorage.service.FileService;
import org.nikita.spingproject.filestorage.service.S3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class FilesController {
    @Autowired
    private FileService fileService;

    @GetMapping
    public String index() {
        return "home";
    }

    @GetMapping("/search")
    public String showSearchform() {
        fileService.createBucketForUser(new User().setId(1).setEmail("sdf").setPassword("dsf").setRole("asdf"));

        return "search";
    }

    @PostMapping("/search")
    public String searchFile() {
        return "search";
    }
}

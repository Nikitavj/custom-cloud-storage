package org.nikita.spingproject.filestorage.controller;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class FilesController {
    @Autowired
    private FileService fileService;

    @GetMapping("/upload")
    public String showFormUpload() {
        return "form_upload";
    }

    @PostMapping("/upload")
    public void upload(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(name = "pathFile", required = false) String pathFile,
                       @RequestParam(name = "file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileService.uploadFile(
                new FileUploadDto()
                .setMultipartFile(file)
                .setPathFile(pathFile)
                .setUserName(userDetails.getUsername())
        );
    }

    @GetMapping
    public String showFilesOfPath(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name= "path", required = false) String path,
                                  Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Map<String, String> files = fileService.findFilesOfDirectory(userDetails.getUsername(), path);
        model.addAttribute("files", files);


        return "home";
    }
}

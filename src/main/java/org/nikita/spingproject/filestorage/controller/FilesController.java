package org.nikita.spingproject.filestorage.controller;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
@RequestMapping
public class FilesController {
    @Autowired
    private FileService fileService;

    @PostMapping("/download")
    public void downloadFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(name="pathFile", required = false) String pathFile,
                             @RequestParam(name="targetFile", required = false) String targetFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileService.downloadFile(userDetails.getUsername(), pathFile, targetFile);
    }

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
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name= "path", required = false) String path,
                                  Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(path == null) {
            path = "";
        }

        Map<String, String> files = fileService.findFilesOfDirectory(userDetails.getUsername(), path);
        model.addAttribute("files", files);

        return "home";
    }
}

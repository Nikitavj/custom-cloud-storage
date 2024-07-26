package org.nikita.spingproject.filestorage.controller;

import io.minio.errors.*;
import org.apache.coyote.BadRequestException;
import org.nikita.spingproject.filestorage.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
@RequestMapping
@Validated
public class FilesController {
    @Autowired
    private FileService fileService;

    @PostMapping("/directory")
    public String createNewFolder(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam String path,
                                  @RequestParam("name_folder") String nameFolder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.createFolder(userDetails.getUsername(), path, nameFolder);
        return "redirect:" + "/?path="+path;
    }

    @PostMapping("/download")
    public String downloadFile(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam String pathFile,
                               @RequestParam String targetFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileService.downloadFile(userDetails.getUsername(), pathFile, targetFile);

        return "redirect:/";
    }

    @PostMapping
    public String upload(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String pathFile,
                         @RequestParam MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {


        fileService.uploadFile(
                new FileUploadDto()
                        .setMultipartFile(file)
                        .setPathFile(pathFile)
                        .setUserName(userDetails.getUsername()));

        return "redirect:/";
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "path", required = false) String path,
                        Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (path == null) {
            path = "";
        }

        Map<String, String> files = fileService.findFilesOfDirectory(userDetails.getUsername(), path);
        model.addAttribute("files", files);
        model.addAttribute("path", path);

        return "home";
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String pathFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileService.deleteFile(userDetails.getUsername(), pathFile);

        return "redirect:/";
    }
}

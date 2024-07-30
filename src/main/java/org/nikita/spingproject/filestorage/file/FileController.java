package org.nikita.spingproject.filestorage.file;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
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

@Controller
@RequestMapping
@Validated
public class FileController {
    @Autowired
    private FileService fileService;



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


    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String pathFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileService.deleteFile(userDetails.getUsername(), pathFile);

        return "redirect:/";
    }
}

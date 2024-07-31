package org.nikita.spingproject.filestorage.file;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileServiceImpl fileServiceImpl;

    @PostMapping
    public String uploadFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("file") MultipartFile multiFile,
                             @RequestParam String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileServiceImpl.uploadFile(new FileUploadDto()
                .setMultipartFile(multiFile)
                .setPathFile(path)
                .setUserName(userDetails.getUsername()));

        return "redirect:/" + "?path=" + path;
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fileServiceImpl.deleteFile(new FileDto()
                .setPath(path)
                .setNameUser(userDetails.getUsername()));
        return "redirect:/";
    }
}

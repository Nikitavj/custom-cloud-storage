package org.nikita.spingproject.filestorage.file;

import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileRenameDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.file.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileServiceImpl fileServiceImpl;

    @SneakyThrows
    @PostMapping
    public String uploadFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("file") MultipartFile[] multFiles,
                             @RequestParam String path) {

        for (MultipartFile multFile : multFiles) {
            fileServiceImpl.uploadFile(new FileUploadDto()
                    .setInputStream(multFile.getInputStream())
                    .setName(multFile.getOriginalFilename())
                    .setPath(path)
                    .setUserName(userDetails.getUsername()));
        }

        return "redirect:/" + "?path=" + path;
    }

    @PutMapping
    public String renameFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("new_name") String newName,
                             @RequestParam String path,
                             @RequestParam(name = "current_path", required = false) String currentPath) {
        fileServiceImpl.renameFile(new FileRenameDto()
                .setNewName(newName)
                .setPath(path)
                .setUserName(userDetails.getUsername()));

        return "redirect:/" + "?path=" + currentPath;
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<InputStreamResource> downloadFile(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestParam String path) {
        FileDownloadDto dto = fileServiceImpl.downloadFile(new FileDto()
                .setUserName(userDetails.getUsername())
                .setPath(path));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(name = "current_path", required = false) String currentPath,
                             @RequestParam String path) {

        fileServiceImpl.deleteFile(new FileDto()
                .setPath(path)
                .setUserName(userDetails.getUsername()));
        return "redirect:/" + "?path=" + currentPath;
    }
}

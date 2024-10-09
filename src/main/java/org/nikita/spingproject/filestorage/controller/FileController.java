package org.nikita.spingproject.filestorage.controller;

import org.nikita.spingproject.filestorage.directory.exception.DirectoryAlreadyExistsException;
import org.nikita.spingproject.filestorage.file.dto.DeleteFileRequest;
import org.nikita.spingproject.filestorage.file.dto.DownloadFileRequest;
import org.nikita.spingproject.filestorage.file.dto.DownloadFileResponse;
import org.nikita.spingproject.filestorage.file.dto.RenameFileRequest;
import org.nikita.spingproject.filestorage.file.exception.FileAlreadyExistsException;
import org.nikita.spingproject.filestorage.file.exception.FileNameException;
import org.nikita.spingproject.filestorage.file.exception.FileRemoveException;
import org.nikita.spingproject.filestorage.file.exception.FileRenameException;
import org.nikita.spingproject.filestorage.service.FileService;
import org.nikita.spingproject.filestorage.service.FileServiceImpl;
import org.nikita.spingproject.filestorage.utils.NameFileValidator;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String path) {
        DownloadFileResponse dto = fileService
                .download(new DownloadFileRequest(path));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + dto.getName());
        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @PutMapping
    public RedirectView renameFile(@RequestParam("new_name") String newName,
                                   @RequestParam String path,
                                   @RequestParam(name = "current_path", required = false) String currentPath,
                                   RedirectAttributes redirectAttributes) {
        String redirectPath = "/";
        try {
            if (currentPath != null && !currentPath.isBlank()) {
                redirectPath = "/?path=" + PathEncoderUtil.encode(currentPath);
            }

            if (newName == null || newName.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Empty field");
                return new RedirectView(redirectPath);
            }

            NameFileValidator.checkFileName(newName);
            fileService.rename(
                    new RenameFileRequest(
                            path,
                            newName.trim()));
        } catch (FileAlreadyExistsException | FileRenameException |
                 FileNameException | UnsupportedEncodingException |
                 DirectoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }

    @DeleteMapping
    public RedirectView deleteFile(@RequestParam(name = "current_path", required = false) String currentPath,
                                   @RequestParam String path,
                                   RedirectAttributes redirectAttributes) {
        String redirectPath = "/";
        try {
            if (currentPath != null && !currentPath.isBlank()) {
                redirectPath = "/?path=" + PathEncoderUtil.encode(currentPath);
            }

            fileService.delete(new DeleteFileRequest(path));
        } catch (FileRemoveException | UnsupportedEncodingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }
}

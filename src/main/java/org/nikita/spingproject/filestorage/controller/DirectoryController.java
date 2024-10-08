package org.nikita.spingproject.filestorage.controller;

import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.file.exception.FileAlreadyExistsException;
import org.nikita.spingproject.filestorage.service.DirectoryService;
import org.nikita.spingproject.filestorage.utils.NameFileValidator;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;

@Controller
@Validated
@RequestMapping("/directory")
public class DirectoryController {
    private final DirectoryService directoryService;

    @Autowired
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> downloadDirectory(
            @RequestParam String path) {
        try {
            DownloadDirResponse response = directoryService
                    .download(new DownloadDirRequest(path));

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + response.getName());
            return ResponseEntity
                    .ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(response.getInputStream()));

        } catch (DirectoryException e) {
            throw new DirectoryDownloadException("Folder download error");
        }
    }

    @PostMapping
    public RedirectView createNewDirectory(@RequestParam("name") String nameFolder,
                                           @RequestParam("current_path") String currentPath,
                                           RedirectAttributes redirectAttributes) {
        String redirectPath = "/";
        try {
            if (currentPath != null && !currentPath.isBlank()) {
                redirectPath = "/?path=" + PathEncoderUtil.encode(currentPath);
            }

            if (nameFolder == null || nameFolder.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Empty field");
                return new RedirectView(redirectPath);
            }

            NameFileValidator.checkDirectoryName(nameFolder);
            DirectoryDto newDirectory = directoryService
                    .create(new CreateDirRequest(
                            currentPath,
                            nameFolder.trim()));

            redirectPath = "/?path=" + PathEncoderUtil.encode(newDirectory.getRelativePath());

        } catch (UnsupportedEncodingException | DirectoryCreatedException |
                 DirectoryNameException | DirectoryAlreadyExistsException |
                 FileAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }

    @DeleteMapping
    public RedirectView deleteDirectory(@RequestParam(name = "current_path", required = false) String currentPath,
                                        @RequestParam("path") String relativePath,
                                        RedirectAttributes redirectAttributes) {

        String redirectPath = "/";
        try {
            if (currentPath != null && !currentPath.isBlank()) {
                redirectPath = "/?path=" + PathEncoderUtil.encode(currentPath);
            }

            directoryService.delete(
                    new DeleteDirRequest(relativePath));
        } catch (DirectoryRemoveException | UnsupportedEncodingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return new RedirectView(redirectPath);
    }

    @PutMapping
    public RedirectView renameDirectory(@RequestParam(value = "new_name", required = false) String newName,
                                        @RequestParam("path") String relativePath,
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

            NameFileValidator.checkDirectoryName(newName);
            directoryService.rename(
                    new RenameDirRequest(
                            relativePath,
                            newName.trim()));
        } catch (DirectoryAlreadyExistsException | DirectoryRenameException |
                 DirectoryNameException | FileAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new RedirectView(redirectPath);
    }
}

package org.nikita.spingproject.filestorage.directory;

import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryCreatedException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryRemoveException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryRenameException;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
@Validated
@RequestMapping("/directory")
public class DirectoryController {
    private DirectoryService directoryService;

    @Autowired
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> downloadDirectory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String path
    ) throws IOException {
        DirDownloadResponse response = directoryService.downloadDirectory(
                new DirDownloadRequest(
                        path,
                        userDetails.getUsername()));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.getName());

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(response.getInputStream()));
    }

    @PostMapping
    public RedirectView createNewFolder(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam("name") String nameFolder,
                                        @RequestParam("current_path") String currentPath,
                                        RedirectAttributes redirectAttributes) {

        String redirectPath = "/?path=" + currentPath;
        if (currentPath == null || currentPath.isBlank()) {
            redirectPath = "/";
        }

        if (nameFolder == null || nameFolder.isBlank()) {
            redirectAttributes.addFlashAttribute("errorCreate", "Empty field");
            return new RedirectView(redirectPath);
        }

        try {
            DirDto newDirectory = directoryService
                    .createNewDirectory(new NewDirDto(
                            currentPath,
                            nameFolder,
                            userDetails.getUsername()));
            return new RedirectView("/?path=" + newDirectory.getRelativePath());

        } catch (DirectoryCreatedException e) {
            redirectAttributes.addFlashAttribute("errorCreate", e.getMessage());
            return new RedirectView(redirectPath);
        }
    }

    @DeleteMapping
    public String deleteFolder(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(name = "current_path", required = false) String currentPath,
                               @RequestParam("path") String relativePath) {

        try {
            directoryService.deleteDirectory(
                    new DeleteDirDto(
                            relativePath,
                            userDetails.getUsername()));
        } catch (DirectoryRemoveException e) {
            throw new RuntimeException(e);
        }

        if (currentPath.isBlank()) {
            return "redirect:/";
        } else {
            return "redirect:/" + "?path=" + currentPath;
        }
    }

    @PutMapping
    public RedirectView renameDirectory(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam(value = "new_name", required = false) String newName,
                                        @RequestParam("path") String relativePath,
                                        @RequestParam(name = "current_path", required = false) String currentPath,
                                        RedirectAttributes redirectAttributes) {

        String redirectPath = "/?path=" + currentPath;
        if (currentPath == null || currentPath.isBlank()) {
            redirectPath = "/";
        }

        if (newName == null || newName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorRenameDir", "Empty field");
            return new RedirectView(redirectPath);
        }

        try {
            directoryService.renameDirectory(
                    new RenameDirDto(
                            relativePath,
                            newName,
                            userDetails.getUsername()));
        } catch (DirectoryRenameException e) {
            redirectAttributes.addFlashAttribute("errorRenameDir", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }
}

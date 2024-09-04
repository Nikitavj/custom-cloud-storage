package org.nikita.spingproject.filestorage.directory;

import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
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
            @RequestParam String path) {
        try {
            DirDownloadResponse response = directoryService
                    .downloadDirectory(new DirDownloadRequest(path));

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.getName());
            return ResponseEntity
                    .ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(response.getInputStream()));

        } catch (GetDirectoryObjectsExcepton | IOException e) {
            throw new DirectoryDownloadException("Folder download error");
        }
    }

    @PostMapping
    public String createNewFolder(@RequestParam("name") String nameFolder,
                                  @RequestParam("current_path") String currentPath,
                                  RedirectAttributes redirectAttributes) {

        String redirectPath = "/?path=" + currentPath;
        if (currentPath == null || currentPath.isBlank()) {
            redirectPath = "/";
        }

        if (nameFolder == null || nameFolder.isBlank()) {
            redirectAttributes.addFlashAttribute("errorCreate", "Empty field");
            return "redirect:" + redirectPath;
        }

        try {
            DirDto newDirectory = directoryService
                    .createNewDirectory(new NewDirDto(
                            currentPath,
                            nameFolder));
            return "redirect:/?path=" + newDirectory.getRelativePath();

        } catch (DirectoryCreatedException e) {
            redirectAttributes.addFlashAttribute("errorCreate", e.getMessage());
            return "redirect:" + redirectPath;
        }
    }

    @DeleteMapping
    public String deleteFolder(@RequestParam(name = "current_path", required = false) String currentPath,
                               @RequestParam("path") String relativePath) {

        try {
            directoryService.deleteDirectory(
                    new DeleteDirDto(relativePath));
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
    public RedirectView renameDirectory(@RequestParam(value = "new_name", required = false) String newName,
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
                            newName));
        } catch (DirectoryRenameException e) {
            redirectAttributes.addFlashAttribute("errorRenameDir", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }
}

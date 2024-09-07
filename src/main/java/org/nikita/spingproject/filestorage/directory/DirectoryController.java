package org.nikita.spingproject.filestorage.directory;

import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.nikita.spingproject.filestorage.utils.NameFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    public RedirectView createNewFolder(@RequestParam("name") String nameFolder,
                                        @RequestParam("current_path") String currentPath,
                                        RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

        String redirectPath = "/";
        if (currentPath != null || !currentPath.isBlank()) {
            redirectPath = "/?path=" + URLEncoder.encode(currentPath, "UTF-8");;
        }

        if (nameFolder == null || nameFolder.isBlank()) {
            redirectAttributes.addFlashAttribute("errorCreate", "Empty field");
            return new RedirectView(redirectPath);
        }

        try {
            NameFileValidator.checkDirectoryName(nameFolder);
            DirDto newDirectory = directoryService
                    .createNewDirectory(new NewDirDto(
                            currentPath,
                            nameFolder));

            String pathUTF8 = URLEncoder.encode(
                    newDirectory.getRelativePath(),
                    "UTF-8");
            redirectPath = "/?path=" + pathUTF8;

        } catch (UnsupportedEncodingException | DirectoryCreatedException | DirectoryNameException |
                 DirectoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorCreate", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }

    @DeleteMapping
    public RedirectView deleteFolder(@RequestParam(name = "current_path", required = false) String currentPath,
                                     @RequestParam("path") String relativePath,
                                     RedirectAttributes redirectAttributes) {
        String redirectPath = "/?path=" + currentPath;
        if (currentPath == null || currentPath.isBlank()) {
            redirectPath = "/";
        }

        try {
            directoryService.deleteDirectory(
                    new DeleteDirDto(relativePath));
        } catch (DirectoryRemoveException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return new RedirectView(redirectPath);
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
            NameFileValidator.checkDirectoryName(newName);
            directoryService.renameDirectory(
                    new RenameDirDto(
                            relativePath,
                            newName));
        } catch (DirectoryAlreadyExistsException | DirectoryRenameException | DirectoryNameException e) {
            redirectAttributes.addFlashAttribute("errorRenameDir", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }
}

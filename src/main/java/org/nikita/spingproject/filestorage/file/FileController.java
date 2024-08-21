package org.nikita.spingproject.filestorage.file;

import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileRenameDto;
import org.nikita.spingproject.filestorage.file.exception.FileRenameException;
import org.nikita.spingproject.filestorage.file.service.FileService;
import org.nikita.spingproject.filestorage.file.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/file")
public class FileController {
    private FileService fileService;

    @Autowired
    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<InputStreamResource> downloadFile(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestParam String path) {
        FileDownloadDto dto = fileService
                .downloadFile(new FileDto(
                        path,
                        userDetails.getUsername()));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @PutMapping
    public RedirectView renameFile(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("new_name") String newName,
                                   @RequestParam String path,
                                   @RequestParam(name = "current_path", required = false) String currentPath,
                                   RedirectAttributes redirectAttributes) {
        String redirectPath = "/?path=" + currentPath;
        if (currentPath == null || currentPath.isBlank()) {
            redirectPath = "/";
        }

        if (newName == null || newName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorRenameFile", "Empty field");
            return new RedirectView(redirectPath);
        }

        try {
            fileService.renameFile(new FileRenameDto(
                    path,
                    newName,
                    userDetails.getUsername()));
        } catch (FileRenameException e) {
            redirectAttributes.addFlashAttribute("errorRenameFile", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }

    @DeleteMapping
    public String deleteFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(name = "current_path", required = false) String currentPath,
                             @RequestParam String path) {

        fileService.deleteFile(new FileDto(
                path,
                userDetails.getUsername()));

        if (currentPath.isBlank()) {
            return "redirect:/";
        } else {
            return "redirect:/" + "?path=" + currentPath;
        }
    }
}

package org.nikita.spingproject.filestorage.directory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.nikita.spingproject.filestorage.directory.dto.DeleteDirDto;
import org.nikita.spingproject.filestorage.directory.dto.DirDto;
import org.nikita.spingproject.filestorage.directory.dto.NewDirDto;
import org.nikita.spingproject.filestorage.directory.dto.RenameDirDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/directory")
public class DirectoryController {
    private DirectoryService directoryService;

    @Autowired
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping
    public String createNewFolder(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name = "current_path", required = false) String currentPath,
                                  @RequestParam("name_folder") @NotNull @NotEmpty String nameFolder) {

        DirDto newDirectory = directoryService
                .createNewDirectory(new NewDirDto(
                        currentPath,
                        nameFolder,
                        userDetails.getUsername()));

        return "redirect:/" + "?path=" + newDirectory.getRelativePath();
    }

    @DeleteMapping
    public String deleteFolder(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(name = "current_path", required = false) String currentPath,
                               @RequestParam("path") @NotNull @NotEmpty String relativePath) {

        directoryService.deleteDirectory(
                new DeleteDirDto(
                        relativePath,
                        userDetails.getUsername()));

        if (currentPath.isBlank()) {
            return "redirect:/";
        } else {
            return "redirect:/" + "?path=" + currentPath;
        }
    }

    @PutMapping
    public String renameDirectory(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam("new_name") @NotNull @NotEmpty String newName,
                                  @RequestParam("path") @NotNull @NotEmpty String relativePath,
                                  @RequestParam(name = "current_path", required = false) String currentPath) {

        directoryService.renameDirectory(
                new RenameDirDto(
                        relativePath,
                        newName,
                        userDetails.getUsername()));

        if (currentPath.isBlank()) {
            return "redirect:/";
        } else {
            return "redirect:/" + "?path=" + currentPath;
        }
    }
}

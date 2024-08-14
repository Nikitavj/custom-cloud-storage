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
    @Autowired
    private DirectoryService directoryService;

    @PostMapping
    public String createNewFolder(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name = "current_path", required = false) String currentPath,
                                  @RequestParam("name_folder") @NotNull @NotEmpty String nameFolder) {

        if (currentPath == null || currentPath.isBlank()) {
            currentPath = "/";
        }

        DirDto newDirectory = directoryService.createNewDirectory(new NewDirDto(currentPath, nameFolder, userDetails.getUsername()));

        return "redirect:/" + "?path=" + newDirectory.getRelativePath();
    }

    @DeleteMapping
    public String deleteFolder(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(name = "current_path", required = false) String currentPath,
                               @RequestParam("path") @NotNull @NotEmpty String relPath) {

        if (currentPath == null || currentPath.isBlank()) {
            currentPath = "/";
        }

        directoryService.deleteDirectory(new DeleteDirDto(relPath, userDetails.getUsername()));

        return "redirect:/" + "?path=" + currentPath;
    }

    @PutMapping
    public String renameDirectory(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam("new_name") @NotNull @NotEmpty String newName,
                                  @RequestParam("path") @NotNull @NotEmpty String relPath,
                                  @RequestParam(name = "current_path", required = false) String currentPath) {

        if (currentPath == null || currentPath.isBlank()) {
            currentPath = "/";
        }

        directoryService.renameDirectory(new RenameDirDto()
                .setNewName(newName)
                .setUserName(userDetails.getUsername())
                .setPreviousPath(relPath));

        return "redirect:/" + "?path=" + currentPath;
    }
}

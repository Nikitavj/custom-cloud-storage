package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StorageController {
    @Autowired
    private DirectoryService directoryService;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "path", required = false) String path,
                        Model model) {
        if (path == null || path.isBlank()) {
            path = "/";
        }

        List<ObjectStorageDto> entities = directoryService.getObjectsDirectory(new ObjectsDirDto(path, userDetails.getUsername()));
        model.addAttribute("objects_dir", entities);
        model.addAttribute("current_path", path);
        return "home";
    }

}

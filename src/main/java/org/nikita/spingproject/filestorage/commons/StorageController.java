package org.nikita.spingproject.filestorage.commons;

import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.breadcrumbs.BreadcrumbsUtil;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.upload.UploadFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
public class StorageController {
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private UploadFilesService uploadFilesService;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "path", required = false) String path,
                        Model model) {

        List<ObjectStorageDto> entities = directoryService.getObjectsDirectory(new ObjectsDirDto(path, userDetails.getUsername()));
        model.addAttribute("objects_dir", entities);
        model.addAttribute("current_path", path);
        model.addAttribute("bread_crumbs", BreadcrumbsUtil.createBreadcrumbs(path));
            return "home";
    }

    @SneakyThrows
    @PostMapping
    public String upload(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam("file") MultipartFile[] multFiles,
                         @RequestParam String path) {

        for (MultipartFile multFile : multFiles) {
            uploadFilesService.upload(new FileUploadDto()
                    .setInputStream(multFile.getInputStream())
                    .setName(multFile.getOriginalFilename())
                    .setPath(path)
                    .setUserName(userDetails.getUsername()));
        }

        if (path.isBlank()) {
            return "redirect:/";
        } else {
            return "redirect:/" + "?path=" + path;
        }
    }
}

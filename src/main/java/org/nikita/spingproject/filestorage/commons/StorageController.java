package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.commons.breadcrumbs.BreadcrumbsUtil;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.upload.UploadFilesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@Controller
public class StorageController {
    private DirectoryService directoryService;
    private UploadFilesServiceImpl uploadFilesService;

    @Autowired
    public StorageController(DirectoryService directoryService, UploadFilesServiceImpl uploadFilesService) {
        this.directoryService = directoryService;
        this.uploadFilesService = uploadFilesService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "path", required = false) String path,
                        Model model) {

        if (userDetails == null) {
            return "home";
        }

        List<ObjectStorageDto> entities = directoryService
                .getObjectsDirectory(new ObjectsDirDto(path));
        model.addAttribute("objects_dir", entities);
        model.addAttribute("current_path", path);
        model.addAttribute("bread_crumbs", BreadcrumbsUtil.createBreadcrumbs(path));
        return "home";
    }

    @PostMapping
    public RedirectView upload(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam("file") MultipartFile[] multFiles,
                               @RequestParam String path,
                               RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return new RedirectView( "home");
        }

        try {


            for (MultipartFile multFile : multFiles) {
                uploadFilesService.upload(
                        FileUploadDto.builder()
                                .inputStream(multFile.getInputStream())
                                .name(multFile.getOriginalFilename())
                                .path(path)
                                .build());
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorUpload", "Error loading object");
        }

        if (path.isBlank()) {
            return new RedirectView( "/");
        } else {
            return new RedirectView("/?path=" + path);
        }
    }
}

package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.commons.breadcrumbs.BreadcrumbsUtil;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirDto;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryCreatedException;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.file.exception.FileUploadException;
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
        String redirectPath = "/?path=" + path;
        if (path == null || path.isBlank()) {
            redirectPath = "/";
        }

        if (userDetails == null) {
            return new RedirectView("/");
        }

        //TODO: проверить имена загружаемых файлов,
        // если имя пустое или содержит недопустимве символы,
        // то вернуться на ту же страницу с ошибкой



        try {
            for (MultipartFile multFile : multFiles) {
                uploadFilesService.upload(
                        FileUploadDto.builder()
                                .inputStream(multFile.getInputStream())
                                .name(multFile.getOriginalFilename())
                                .path(path)
                                .build());
            }
        } catch (FileUploadException | DirectoryCreatedException | IOException e) {
            redirectAttributes.addFlashAttribute("errorUpload", "Error upload object");
        }
        return new RedirectView(redirectPath);
    }
}

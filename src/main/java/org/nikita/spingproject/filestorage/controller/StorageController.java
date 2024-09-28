package org.nikita.spingproject.filestorage.controller;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.breadcrumbs.BreadcrumbsUtil;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirRequest;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryAlreadyExistsException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryCreatedException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryNameException;
import org.nikita.spingproject.filestorage.file.dto.FilesUploadDto;
import org.nikita.spingproject.filestorage.file.exception.FileAlreadyExistsException;
import org.nikita.spingproject.filestorage.file.exception.FileNameException;
import org.nikita.spingproject.filestorage.file.exception.FileUploadException;
import org.nikita.spingproject.filestorage.service.DirectoryService;
import org.nikita.spingproject.filestorage.service.UploadFilesServiceImpl;
import org.nikita.spingproject.filestorage.utils.NameFileValidator;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
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
                .getObjectsOfDir(new ObjectsDirRequest(path));
        model.addAttribute("objects_dir", entities);
        model.addAttribute("current_path", path);
        model.addAttribute("bread_crumbs", BreadcrumbsUtil.createBreadcrumbs(path));
        return "home";
    }

    @PostMapping
    public RedirectView upload(@RequestParam("file") MultipartFile[] multFiles,
                               @RequestParam String path,
                               RedirectAttributes redirectAttributes) {
        String redirectPath = "/";
        try {
            if (path != null && !path.isBlank()) {
                redirectPath = "/?path=" + PathEncoderUtil.encode(path);
            }
            for (MultipartFile multFile : multFiles) {
                if (multFile.getOriginalFilename().isBlank()) {
                    throw new FileUploadException();
                }
                NameFileValidator.checkUploadName(multFile.getOriginalFilename());
            }
            uploadFilesService.upload(
                    new FilesUploadDto(multFiles, path));

        } catch (IOException | FileUploadException | DirectoryCreatedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error upload object");
        } catch (FileAlreadyExistsException | DirectoryAlreadyExistsException | DirectoryNameException | FileNameException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new RedirectView(redirectPath);
    }
}

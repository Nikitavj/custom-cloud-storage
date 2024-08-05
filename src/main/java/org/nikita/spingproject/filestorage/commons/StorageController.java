package org.nikita.spingproject.filestorage.commons;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.directory.dto.FolderDto;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirectoryDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class StorageController {
    @Autowired
    private DirectoryService directoryService;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "path", required = false) String path,
                        Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        ObjectsDirectoryDto objectsDir = directoryService.listDirectoryObjects(new FolderDto()
                .setUserName(userDetails.getUsername())
                .setPath(path));
        model.addAttribute("objects_dir", objectsDir.getList());
        model.addAttribute("current_path", path);
        return "home";
    }

}

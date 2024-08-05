package org.nikita.spingproject.filestorage.directory;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.directory.dto.FolderDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/directory")
public class DirectoryController {
    @Autowired
    private DirectoryService directoryService;

    @PostMapping
    public String createNewFolder(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name="current_path", required = false) String currentPath,
                                  @RequestParam("name_folder") String nameFolder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Folder folder = directoryService.createNewFolder(new FolderDto()
                .setName(nameFolder)
                .setPath(currentPath)
                .setUserName(userDetails.getUsername()));

        return "redirect:/" + "?path=" + folder.getLink();
    }

    @DeleteMapping
    public String deleteFolder(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(name="current_path", required = false) String currentPath,
                               @RequestParam String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        directoryService.deleteFolder(new FolderDto()
                .setUserName(userDetails.getUsername())
                .setPath(path));

        return "redirect:/" + "?path=" + currentPath;
    }
}

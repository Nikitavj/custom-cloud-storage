package org.nikita.spingproject.filestorage.directory.service;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.directory.DirPathService;
import org.nikita.spingproject.filestorage.directory.s3api.DirectoryS3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RenameDirectoryService {
    @Autowired
    private DirectoryS3Api directoryS3Api;
    @Autowired
    private DirPathService dirPathService;

    @SneakyThrows
    public void rename(String previousRelPath, String newName, String userName) {
        String prevAbsolutPath = dirPathService.createAbsolutPath(previousRelPath, userName);

        String targetRelativePath = dirPathService.createRelativePathRenameDir(previousRelPath, newName);
        String targetAbsolutPath = dirPathService.createAbsolutPath(targetRelativePath, userName);

        List<Item> objectsPaths = new ArrayList<>();
        Iterable<Result<Item>> results = directoryS3Api.getObjectsDirectoryRecursive(prevAbsolutPath);
        for (Result<Item> itemResult : results) {
            Item item = itemResult.get();
            if (!item.isDir()) {



            }
        }

        System.out.println();
    }

}

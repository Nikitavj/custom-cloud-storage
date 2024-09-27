package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryDownloadException;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.s3manager.S3FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipArchiver {
    private final S3FileManager s3FileManager;

    @Autowired
    public ZipArchiver(S3FileManager s3FileManager) {
        this.s3FileManager = s3FileManager;
    }

    public void zipDirectory(Directory dir, String prefixPath, ZipOutputStream zos) throws IOException {
        List<File> files = dir.getFiles();
        List<Directory> directories = dir.getDirectories();

        for (Directory directory : directories) {
            String basePath = prefixPath + "/" + directory.getName();
            zipDirectory(directory, basePath, zos);
        }

        for (File file : files) {
            File fileWithIS = s3FileManager.get(file.getPath());

            try (InputStream is = fileWithIS.getInputStream()) {
                ZipEntry zipEntry = new ZipEntry(prefixPath + "/" + file.getName());
                zos.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    zos.write(bytes);
                }
                zos.closeEntry();
            }
        }
    }
}

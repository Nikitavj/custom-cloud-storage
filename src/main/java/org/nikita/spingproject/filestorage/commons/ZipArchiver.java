package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.directory.Directory;
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
    private static final String SEPARATOR = "/";

    @Autowired
    public ZipArchiver(S3FileManager s3FileManager) {
        this.s3FileManager = s3FileManager;
    }

    public void zipDirectory(Directory dir, String prefixPath, ZipOutputStream zos) throws IOException {
        List<File> files = dir.getFiles();
        List<Directory> directories = dir.getDirectories();

        for (Directory directory : directories) {
            String basePath = String.join("",
                    prefixPath,
                    SEPARATOR,
                    directory.getName());
            zipDirectory(directory, basePath, zos);
        }

        for (File file : files) {
            File fileWithIS = s3FileManager.get(file.getPath());

            try (InputStream is = fileWithIS.getInputStream()) {
                String filePath = String.join("",
                        prefixPath,
                        SEPARATOR,
                        file.getName());
                ZipEntry zipEntry = new ZipEntry(filePath);
                zos.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = is.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                zos.closeEntry();
            }
        }
    }
}

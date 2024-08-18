package org.nikita.spingproject.filestorage.file.dao;

import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.s3Api.FileS3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileDaoImpl implements FileDao {
    private FileS3Api fileS3Api;

    @Autowired
    public FileDaoImpl(FileS3Api fileS3Api) {
        this.fileS3Api = fileS3Api;
    }

    @Override
    public void add(File file) {
        fileS3Api.putFile(
                createMetaDataFile(
                        file.getName(),
                        file.getRelativePath()),
                file.getAbsolutePath(),
                file.getInputStream());
    }

    @Override
    public void remove(String absolutePath) {
        fileS3Api.deleteFile(absolutePath);
    }

    @Override
    public InputStream get(String absolutePath) {
        return fileS3Api.downloadFile(absolutePath);
    }

    @Override
    public void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath, String name) {
        fileS3Api.copyFile(
                prevAbsolutePath,
                targetAbsolutePath,
                createMetaDataFile(name, relativePath));
    }

    private Map<String, String> createMetaDataFile(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("file", "");
        return metaData;
    }
}

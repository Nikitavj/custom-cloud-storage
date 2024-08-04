package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Repository
public interface DirectoryDao {

    void createFolder(Map<String, String> metaData, String path);

    Iterable<Result<Item>> getObjectsDirectory(String path);

    void deleteFolder(Folder folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

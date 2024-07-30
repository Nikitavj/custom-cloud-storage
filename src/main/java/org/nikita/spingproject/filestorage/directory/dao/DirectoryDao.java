package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
public interface DirectoryDao {
    void createFolder(Folder folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Iterable<Result<Item>> getObjectsDirectory(Folder folder);
}

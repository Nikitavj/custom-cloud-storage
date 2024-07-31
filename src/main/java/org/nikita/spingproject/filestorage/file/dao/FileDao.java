package org.nikita.spingproject.filestorage.file.dao;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.File;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface FileDao {

    void putFile(File file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

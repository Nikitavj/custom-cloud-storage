package org.nikita.spingproject.filestorage.service;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface FileService {
    void uploadFile(FileUploadDto dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

package org.nikita.spingproject.filestorage.file.exception;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }
}

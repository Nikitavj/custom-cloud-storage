package org.nikita.spingproject.filestorage.file.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException() {
    }

    public FileUploadException(String message) {
        super(message);
    }
}

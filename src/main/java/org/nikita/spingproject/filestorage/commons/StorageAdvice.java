package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.directory.exception.DirectoryDownloadException;
import org.nikita.spingproject.filestorage.file.exception.FileDownloadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StorageAdvice {

    @ExceptionHandler(value = {FileDownloadException.class, DirectoryDownloadException.class})
    public ResponseEntity<?> handleDownloadException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}

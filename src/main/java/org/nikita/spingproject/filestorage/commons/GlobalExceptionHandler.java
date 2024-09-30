package org.nikita.spingproject.filestorage.commons;

import org.nikita.spingproject.filestorage.commons.exception.StorageException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryDownloadException;
import org.nikita.spingproject.filestorage.file.exception.FileDownloadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    @ExceptionHandler(value = {FileDownloadException.class, DirectoryDownloadException.class})
    public ResponseEntity<?> handleDownloadException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeUploadFile(Model model) {
        model.addAttribute("errorMessage", "Upload size exceeded maximum: " + maxSize);
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        return "error";
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StorageException.class)
    public String handleStorageError(Model model) {
        model.addAttribute("errorMessage", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "error";
    }
}

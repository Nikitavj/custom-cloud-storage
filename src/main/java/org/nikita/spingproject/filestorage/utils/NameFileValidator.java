package org.nikita.spingproject.filestorage.utils;

import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryNameException;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryRenameException;
import org.nikita.spingproject.filestorage.file.exception.FileNameException;
import org.nikita.spingproject.filestorage.file.exception.FileRenameException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameFileValidator {
    private static final String SEPARATOR = "/";
    private static final String REGEX_INVALID_NAME = "[<>:\"/|?*\\\\]";

    public static void checkUploadName(String name) {
        if (!name.contains(SEPARATOR)) {
                checkFileName(name);
        }
        List<String> partsName = new LinkedList<>(Arrays.asList(name.split(SEPARATOR)));
        checkFileName(partsName.removeLast());

        for (String part: partsName) {
            checkDirectoryName(part);
        }
    }

    public static void checkFileName(String name) {
        Pattern pattern = Pattern.compile(REGEX_INVALID_NAME);
        Matcher matcher = pattern.matcher(name);
        if (matcher.find() || name.isBlank()) {
            throw new FileNameException("Invalid file name: " + name + " (do not use <>:/\\|?*\")");
        }
    }

    public static void checkDirectoryName(String name) {
        Pattern pattern = Pattern.compile(REGEX_INVALID_NAME);
        Matcher matcher = pattern.matcher(name);
        if(matcher.find() || name.isBlank()) {
            throw new DirectoryNameException("Invalid folder name: " + name + " (do not use <>:/\\|?*\")");
        }
    }
}

package org.nikita.spingproject.filestorage.utils;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {
    private static final String FORMAT_DATE = "HH:mm dd.MM.yyyy";

    public static String format(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
        return date.format(formatter);
    }
}

package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date){
        return date == null ? null : date.format(FORMATTER);
    }

    public static String timeFormat(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return LocalDateTime.now().format(timeFormatter);
    }
}

package com.example.SevMerge.core.util;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.sql.Timestamp;
import java.util.Date;


public class MyDateUtil {

    public static String timestampFormat(Timestamp timestamp) {
        Date currentDate = new Date(timestamp.getTime());
        return DateFormatUtils.format(currentDate, "yyyy-MM-dd HH:mm");
    }
}

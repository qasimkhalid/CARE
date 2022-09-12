package operations;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Time {
    public static long currentTimeInMillis() {
        return System.currentTimeMillis();
    }


    private static String getCurrentTimeStampInHumanReadableFormat() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }
}

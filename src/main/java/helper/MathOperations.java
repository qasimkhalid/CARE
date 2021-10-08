package helper;

import java.text.SimpleDateFormat;
import java.util.Date;

final public class MathOperations
{
    public static long getExtraTime(float area, Integer density, float areaPerPerson) {
        return (long) (areaPerPerson * density - area) * 1000;
    }

    private static String getCurrentTimeStamp() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    public static int randomNumberSelector(int max, int min) {
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }
}

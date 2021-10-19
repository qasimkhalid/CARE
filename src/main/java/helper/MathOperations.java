package helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

final public class MathOperations
{
    public static long getExtraTime(float area, Integer density, float areaPerPerson) {
        return (long) (areaPerPerson * density - area) * 1000;
    }

    private static String getCurrentTimeStamp() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    public static int getRandomNumberInRange(int max, int min) {
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }

    public static float getRandomNumberInRange(float max, float min) {
        Random random = new Random();
        return min + random.nextFloat() * (max - min);
    }

    public static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public static int getRandomNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    public String MillisecondsToHumanTime( int millis ) {
        int hour = ((millis / 1000) / 3600);
        int minute = (((millis / 1000) / 60) % 60);
        int second = ((millis / 1000) % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}

package operations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

final public class MathOperations
{
    private static final Random random = new Random(4124);
    public static long getExtraTime(float area, Integer density, float areaPerPerson) {
        return (long) (areaPerPerson * density - area) * 1000;
    }


    public static double getRandomNumberInRange(double max, double min) {
        return min + random.nextFloat() * (max - min);
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static int getRandomNumber(int size) {
        return random.nextInt(size);
    }

    public String MillisecondsToHumanTime( int millis ) {
        int hour = ((millis / 1000) / 3600);
        int minute = (((millis / 1000) / 60) % 60);
        int second = ((millis / 1000) % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}

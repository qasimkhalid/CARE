package operations;


import java.util.Random;

public class MathOperations
{
    private static Random random;
    public static long getExtraTime(float area, Integer density, float areaPerPerson) {
        return (long) (areaPerPerson * density - area) * 1000;
    }

    public static double getRandomNumberInRange(double max, double min) {
        return min + random.nextFloat() * (max - min);
    }

    public boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static int getRandomNumber(int size) {
        return random.nextInt(size);
    }

    public static void setSeedForRandom(int seedValue) {
        random = new Random(seedValue);
    }


    public String MillisecondsToHumanTime( int millis ) {
        int hour = ((millis / 1000) / 3600);
        int minute = (((millis / 1000) / 60) % 60);
        int second = ((millis / 1000) % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}

package com.artamonov.millionplanets.utils;

import java.util.Random;

public class RandomUtils {
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    public static String getRandomDebrisName(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static int getRandomCoordinate() {
        Random r = new Random();
        return r.nextInt(600 - 500) + 500;
    }

    public static int getRandomDebrisIron() {
        Random r = new Random();
        int min = 100;
        int max = 300;
        return r.nextInt(max - min + 1) + min;
    }
}

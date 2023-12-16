package com.demo.shop.core;

import java.util.List;
import java.util.Random;

public class Utils {

    public static int getRandomIntWithinRange(int min, int max) {
        Random random = new Random();
        return random.ints(min, max + 1)
                .findFirst()
                .getAsInt();
    }


    public static int getRandomElement(List list) {
        return getRandomIntWithinRange(0, list.size() - 1);
    }

    public static int getRandomPositiveInt(int max) {
        return getRandomIntWithinRange(1, max);
    }

}

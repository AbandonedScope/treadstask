package com.mahanko.threadstask.util;

import java.util.Random;

public class CustomTimeRandomGenerator {
    private static Random random = new Random();
    private CustomTimeRandomGenerator() {
    }

    public static int random(int leftBound, int rightBound) {
        return random.nextInt() % (rightBound - leftBound) + leftBound;
    }
}

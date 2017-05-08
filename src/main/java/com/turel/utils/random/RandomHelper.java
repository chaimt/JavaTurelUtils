package com.turel.utils.random;

/**
 * Created by Chaim on 08/05/2017.
 */
public class RandomHelper {
    public static int randomInRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

}

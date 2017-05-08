package com.turel.utils.random;

public class RandomHelper {
	public static int randomInRange(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

}

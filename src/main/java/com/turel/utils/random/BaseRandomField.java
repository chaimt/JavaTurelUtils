package com.turel.utils.random;

import java.util.Random;

public interface BaseRandomField {
	public Object generateRandom(Class<?> clazz, Random random);
	public void setClassLoader(ClassLoader classLoader);
}

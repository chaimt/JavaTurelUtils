package com.turel.utils.random;

import java.util.Random;

/**
 * Created by Chaim on 08/05/2017.
 */
public interface BaseRandomField {
    public Object generateRandom(Class<?> clazz, Random random);
    public void setClassLoader(ClassLoader classLoader);
}

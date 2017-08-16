package com.turel.utils;

/**
 * same interface as Supplier but removes the need for try catch i lamda throws typed exception
 * @param <T>
 */
public interface UncheckSupplier<T> {
    T get() throws Exception;
}

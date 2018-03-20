package com.turel.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * object to help for cases of anonymous classes and lambdas
 * @param <T>
 */
@Getter
@Setter
public class ObjectWrapper<T> {
	private T value = null;

	public boolean isNull() {
		return value==null;
	}
}


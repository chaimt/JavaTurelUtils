package com.turel.utils.random;

import java.lang.reflect.Field;

/**
 * Created by Chaim on 08/05/2017.
 */
public class EnumHelper {

    @SuppressWarnings("unchecked")
    public static Enum<?> getRandomValue(Class<Enum> enumType) {
        Field[] flds = enumType.getDeclaredFields();
        String enumName = null;
        // count const names
        int enumCount = 0;
        for (Field f : flds) {
            if (f.isEnumConstant()) {
                enumCount++;
            }
        }
        int enumRandomInRange = RandomHelper.randomInRange(0, enumCount - 1);
        int pos = 0;
        for (Field f : flds) {
            if (f.isEnumConstant()) {
                if (f.isEnumConstant()) {
                    if (pos == enumRandomInRange) {
                        enumName = f.getName();
                        break;
                    }
                    pos++;
                }
            }
        }
        return enumName == null ? null : Enum.valueOf(enumType, enumName);
    }

    @SuppressWarnings("unchecked")
    public static Enum<?> getFirstValue(@SuppressWarnings("rawtypes") Class<Enum> enumType) {
        Field[] flds = enumType.getDeclaredFields();
        for (Field f : flds) {
            if (f.isEnumConstant()) {
                return Enum.valueOf(enumType, f.getName());
            }
        }
        return null;
    }

}


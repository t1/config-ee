package com.github.t1.configee;

import org.joda.convert.StringConvert;

public class Converter {
    public static final StringConvert CONVERT = new StringConvert();

    public static <T> T fromString(Class<T> type, String stringValue) {
        return CONVERT.convertFromString(type, stringValue);
    }

}

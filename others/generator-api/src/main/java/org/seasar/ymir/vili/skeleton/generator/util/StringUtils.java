package org.seasar.ymir.vili.skeleton.generator.util;

import java.beans.Introspector;

public class StringUtils {
    private StringUtils() {
    }

    public static String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String decapitalize(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        return Introspector.decapitalize(string);
    }
}

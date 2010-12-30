package org.seasar.ymir.vili.skeleton.generator;

public class GeneratorUtils {
    private GeneratorUtils() {
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException("Can't instanciate: class="
                    + clazz.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Can't instanciate: class="
                    + clazz.getName(), ex);
        }
    }
}

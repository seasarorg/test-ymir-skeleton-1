package org.seasar.ymir.vili.skeleton.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.seasar.ymir.vili.skeleton.generator.enm.GUIType;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
public @interface GUI {
    String displayName();

    String description() default "";

    GUIType type() default GUIType.AUTO;
}

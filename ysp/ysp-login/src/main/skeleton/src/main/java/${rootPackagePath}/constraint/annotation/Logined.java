package ${rootPackageName}.constraint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ${rootPackageName}.constraint.LoginedConstraint;
import ${rootPackageName}.enm.PersonType;

import org.seasar.ymir.constraint.ConstraintType;
import org.seasar.ymir.constraint.annotation.ConstraintAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
@ConstraintAnnotation(type = ConstraintType.PERMISSION, component = LoginedConstraint.class)
public @interface Logined {
    PersonType[] value() default {};
}

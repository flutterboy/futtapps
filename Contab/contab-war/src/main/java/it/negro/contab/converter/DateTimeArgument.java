package it.negro.contab.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DateTimeArgument {

    int plusDays() default 0;
    int plusYears() default 0;
    long plusMillis() default 0;
}

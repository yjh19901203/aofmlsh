package com.sh.mlshsettlement.common.logaspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAnnotion {
    boolean result() default false;
    String method() default "";
    boolean ip() default false;
}

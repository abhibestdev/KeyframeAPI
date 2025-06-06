package me.abhi.keyframeapi.command.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Flag {
    public static final Pattern FLAG_PATTERN = Pattern.compile("(-)([a-zA-Z])([\\w]*)?");

    String[] value();

    boolean defaultValue() default false;

    String description() default "";

}

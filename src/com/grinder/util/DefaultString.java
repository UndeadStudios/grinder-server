package com.grinder.util;

//DefaultString.java:
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultString {
 String value();
}

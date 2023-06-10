package com.grinder.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class JinDefault {

    public void setDefaults() {
        for (Field f : getClass().getFields()) {
            f.setAccessible(true);
            try {
                if (f.get(this) == null) {
                    f.set(this, getDefaultValueFromAnnotation(f.getAnnotations()));
                }
            } catch (IllegalAccessException e) { // shouldn't happen because I used setAccessible
            }
        }
    }

    private Object getDefaultValueFromAnnotation(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a instanceof DefaultString)
                return ((DefaultString)a).value();
            if (a instanceof DefaultInteger)
                return ((DefaultInteger)a).value();
        }
        return null;
    }

}

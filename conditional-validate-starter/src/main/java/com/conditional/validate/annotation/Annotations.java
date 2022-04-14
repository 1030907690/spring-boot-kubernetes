package com.conditional.validate.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Annotations {
    ConditionalValidateField[] value();
}
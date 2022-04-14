package com.conditional.validate.annotation;


import com.conditional.validate.aspectj.ConditionalValidateAspect;
import com.conditional.validate.aspectj.action.impl.IfEqNotNullHandleImpl;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {ConditionalValidateAspect.class, IfEqNotNullHandleImpl.class})
public @interface EnableConditionalValidate {
}

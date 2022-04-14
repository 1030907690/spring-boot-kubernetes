package com.conditional.validate.annotation;


import com.conditional.validate.aspectj.ConditionalValidateAspect;
import com.conditional.validate.aspectj.action.impl.IfEqNotNullHandleImpl;
import com.conditional.validate.importselector.ConditionalValidateAspectImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConditionalValidateAspectImportSelector.class)
public @interface EnableConditionalValidate {
}

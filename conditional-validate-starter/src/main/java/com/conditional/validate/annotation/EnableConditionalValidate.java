package com.conditional.validate.annotation;


import com.conditional.validate.importbean.ConditionalValidateAspectImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConditionalValidateAspectImportBeanDefinitionRegistrar.class)
public @interface EnableConditionalValidate {
}

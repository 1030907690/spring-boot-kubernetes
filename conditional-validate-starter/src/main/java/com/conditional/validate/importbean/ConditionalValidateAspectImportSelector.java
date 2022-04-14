package com.conditional.validate.importbean;

import com.conditional.validate.aspectj.ConditionalValidateAspect;
import com.conditional.validate.aspectj.action.impl.IfEqNotNullHandleImpl;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/***
 * zzq
 * 2022年4月14日14:38:14
 * 导入Bean，注解驱动的方式
 * */
public class ConditionalValidateAspectImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{ConditionalValidateAspect.class.getName(), IfEqNotNullHandleImpl.class.getName()};
    }
}

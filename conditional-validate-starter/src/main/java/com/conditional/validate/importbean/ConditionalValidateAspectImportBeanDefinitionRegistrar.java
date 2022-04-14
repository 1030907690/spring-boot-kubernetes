package com.conditional.validate.importbean;

import com.conditional.validate.aspectj.ConditionalValidateAspect;
import com.conditional.validate.aspectj.action.impl.IfEqNotNullHandleImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/***
 * zzq
 * 2022年4月14日16:57:39
 * 导入Bean,为了兼容低版本Spring代码
 * */
public class ConditionalValidateAspectImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        String [] beans = new String[]{ConditionalValidateAspect.class.getName(), IfEqNotNullHandleImpl.class.getName()};
        for (String bean : beans) {
            BeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClassName(bean);
            beanDefinitionRegistry.registerBeanDefinition(bean,beanDefinition);
        }
    }
}

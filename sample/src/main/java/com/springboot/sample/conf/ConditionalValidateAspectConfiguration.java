package com.springboot.sample.conf;

import com.conditional.validate.aspectj.ConditionalValidateAspect;
import org.springframework.context.annotation.Bean;

//@Configuration
public class ConditionalValidateAspectConfiguration {

    @Bean
    public ConditionalValidateAspect conditionalValidateAspect(){
        return new ConditionalValidateAspect();
    }
}

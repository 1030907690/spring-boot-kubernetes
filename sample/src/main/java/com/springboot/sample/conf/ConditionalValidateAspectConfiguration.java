package com.springboot.sample.conf;

import com.springboot.sample.aspectj.ConditionalValidateAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ConditionalValidateAspectConfiguration {

    @Bean
    public ConditionalValidateAspect conditionalValidateAspect(){
        return new ConditionalValidateAspect();
    }
}

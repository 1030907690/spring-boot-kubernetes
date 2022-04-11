package com.springboot.sample.aspectj.action;

import com.springboot.sample.annotation.ConditionalValidateField;
import com.springboot.sample.aspectj.ConditionalValidateAspect;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;

import java.util.Map;

public interface ValidateHandle {

    void doValidate(Map<String, Class> fieldClzMap, ExpressionParser parser, ConditionalValidateAspect.ConditionalValidateFieldInfo conditionalValidateFieldInfo, EvaluationContext context, String[] paramsName);
}

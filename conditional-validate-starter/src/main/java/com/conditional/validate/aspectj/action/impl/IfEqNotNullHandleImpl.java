package com.conditional.validate.aspectj.action.impl;

import com.conditional.validate.annotation.ConditionalValidateField;
import com.conditional.validate.aspectj.ConditionalValidateAspect;
import com.conditional.validate.aspectj.action.ValidateHandle;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

/*
 * 如果相等，判断是否为空
 * */
@Service
public class IfEqNotNullHandleImpl extends AbstractHandleImpl implements ValidateHandle {


    @Override
    public void doValidate(Map<String, Class> fieldClzMap, ExpressionParser parser, ConditionalValidateAspect.ConditionalValidateFieldInfo conditionalValidateFieldInfo, EvaluationContext context, String[] paramsName) {
        ConditionalValidateField conditionalValidateField = conditionalValidateFieldInfo.getConditionalValidateField();
        // 判断该字段类型
        Class originalClz = fieldClzMap.get(conditionalValidateFieldInfo.getFieldName());
        //TODO 只写了Integer String类型的
        if (Integer.class.getSimpleName().equals(originalClz.getSimpleName())) {
            Expression expression = parser.parseExpression(DEFAULT_SPEL_EXPRESSION_PREFIX + paramsName[0] + SPOT + conditionalValidateFieldInfo.getFieldName());
            Integer originalValue = expression.getValue(context, Integer.class);
            if (!StringUtils.isEmpty(conditionalValidateField.ifValue())) {
                // 如果是相等的
                if (Integer.valueOf(conditionalValidateField.ifValue()).equals(originalValue)) {
                    Expression relationExpression = parser.parseExpression(DEFAULT_SPEL_EXPRESSION_PREFIX + paramsName[0] + SPOT + conditionalValidateField.relationField());
                    String relationField = conditionalValidateField.relationField();
                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                }
            } else {
                // 为空的情况,有可能要求原字段为空，关联字段不能为空的情况；判断都是空就校验
                ifEmptyUnionNotNull(conditionalValidateField, originalValue, parser, context, fieldClzMap, paramsName);
            }
        } else if (String.class.getSimpleName().equals(originalClz.getSimpleName())) {
            Expression expression = parser.parseExpression(DEFAULT_SPEL_EXPRESSION_PREFIX + paramsName[0] + SPOT + conditionalValidateFieldInfo.getFieldName());
            String originalValue = expression.getValue(context, String.class);
            if (!StringUtils.isEmpty(conditionalValidateField.ifValue())) {
                // 如果是相等的
                if (conditionalValidateField.ifValue().equals(originalValue)) {
                    Expression relationExpression = parser.parseExpression(DEFAULT_SPEL_EXPRESSION_PREFIX + paramsName[0] + SPOT + conditionalValidateField.relationField());
                    String relationField = conditionalValidateField.relationField();
                    Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
                    Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
                }
            } else {
                // 为空的情况,有可能要求原字段为空，关联字段不能为空的情况；判断都是空就校验
                ifEmptyUnionNotNull(conditionalValidateField, originalValue, parser, context, fieldClzMap, paramsName);
            }
        } else {
            throw new UnsupportedOperationException("暂未支持的数据类型" + originalClz.getSimpleName());
        }
    }

    private void ifEmptyUnionNotNull(ConditionalValidateField conditionalValidateField, Object originalValue, ExpressionParser parser, EvaluationContext context, Map<String, Class> fieldClzMap, String[] paramsName) {
        if (StringUtils.isEmpty(conditionalValidateField.ifValue()) && StringUtils.isEmpty(originalValue)) {
            Expression relationExpression = parser.parseExpression(DEFAULT_SPEL_EXPRESSION_PREFIX + paramsName[0] + SPOT + conditionalValidateField.relationField());
            String relationField = conditionalValidateField.relationField();
            Object value = relationExpression.getValue(context, fieldClzMap.get(relationField));
            Assert.isTrue(!StringUtils.isEmpty(value), conditionalValidateField.message());
        }
    }
}

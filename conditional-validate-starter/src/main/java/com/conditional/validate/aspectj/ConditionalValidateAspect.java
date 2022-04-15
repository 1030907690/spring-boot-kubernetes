package com.conditional.validate.aspectj;

import com.conditional.validate.annotation.ConditionalValidate;
import com.conditional.validate.annotation.ConditionalValidateField;
import com.conditional.validate.aspectj.action.ValidateHandle;
import com.conditional.validate.aspectj.action.impl.AbstractHandleImpl;
import com.conditional.validate.aspectj.action.impl.IfEqNotNullHandleImpl;
import com.conditional.validate.constant.ValidateFieldAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
//@Component
public class ConditionalValidateAspect implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //将方法参数纳入Spring管理
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    //解析spel表达式
    private final ExpressionParser parser = new SpelExpressionParser();

    private final Map<Integer, ValidateHandle> validateFieldActionHandleMapping = new HashMap<>();

    /**
     * 属性的缓存
     **/
    private final Map<String, List<Field>> allFieldCache = new ConcurrentHashMap<>();


    @Resource
    private ApplicationContext applicationContext;

    @Before("@annotation(conditionalValidate)")
    public void doBefore(JoinPoint joinPoint, ConditionalValidate conditionalValidate) throws Throwable {
        //获取参数对象数组
        Object[] args = joinPoint.getArgs();
        Assert.notEmpty(args, "没有参数");
        Assert.isTrue(args.length <= 1, "只能有一个参数");

        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();


        //获取方法参数名
        String[] paramsName = discoverer.getParameterNames(method);
        //将参数纳入Spring管理
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < paramsName.length; len++) {
            context.setVariable(paramsName[len], args[len]);
        }

        Object firstParams = args[0];
        if (!StringUtils.isEmpty(firstParams)) {
            List<Field> allFields = getAllFields(firstParams, paramsName, method);

            // 把要校验的找到
            List<ConditionalValidateFieldInfo> validateFieldList = new ArrayList<>();
            // 字段类型
            Map<String, Class> fieldClzMap = new HashMap<>();
            findAnnotationFieldAndClass(allFields, fieldClzMap, validateFieldList);


            // 执行校验动作，这块要分很多种情况处理
            validateFieldList.forEach(conditionalValidateFieldInfo -> {
                if (!StringUtils.isEmpty(conditionalValidateFieldInfo)) {
                    ConditionalValidateField conditionalValidateField = conditionalValidateFieldInfo.getConditionalValidateField();
                    // 这个地方可以使用策略模式优化下，共性的地方用模板方法
                    doValidate(conditionalValidateField, fieldClzMap, parser, conditionalValidateFieldInfo, context, paramsName);
                }
            });

        }


    }

    private List<Field> getAllFields(Object firstParams, String[] paramsName, Method method) {
        List<Field> allFields = null;
        String fieldCacheKey = firstParams.getClass().getName() + AbstractHandleImpl.SPOT + method.getName() + AbstractHandleImpl.SPOT + paramsName[0];
        if (!allFieldCache.containsKey(fieldCacheKey)) {
            allFields = getAllFields(firstParams);
            allFieldCache.put(fieldCacheKey,allFields);
        } else {
            allFields = allFieldCache.get(fieldCacheKey);
        }
        Assert.isTrue(!CollectionUtils.isEmpty(allFields), paramsName[0] + "没有属性");
        return allFields;
    }

    private void doValidate(ConditionalValidateField conditionalValidateField, Map<String, Class> fieldClzMap, ExpressionParser parser, ConditionalValidateFieldInfo conditionalValidateFieldInfo, EvaluationContext context, String[] paramsName) {
        ValidateHandle validateHandle = validateFieldActionHandleMapping.get(conditionalValidateField.action());
        Assert.isTrue(!StringUtils.isEmpty(validateHandle), "不能处理的类型" + conditionalValidateField.action());
        validateHandle.doValidate(fieldClzMap, parser, conditionalValidateFieldInfo, context, paramsName);
    }


    private void findAnnotationFieldAndClass(List<Field> allFields, Map<String, Class> fieldClzMap, List<ConditionalValidateFieldInfo> validateFieldList) {
        allFields.forEach(field -> {
            Set<ConditionalValidateField> conditionalValidateFields = AnnotationUtils.getRepeatableAnnotations(field, ConditionalValidateField.class);
//            ConditionalValidateField [] conditionalValidateFields = field.getAnnotationsByType(ConditionalValidateField.class);
            String fieldName = field.getName();
            for (ConditionalValidateField conditionalValidateField : conditionalValidateFields) {
                if (!StringUtils.isEmpty(conditionalValidateField)) {
                    validateFieldList.add(new ConditionalValidateFieldInfo(fieldName, conditionalValidateField));
                }
            }
            fieldClzMap.put(fieldName, field.getType());
        });
    }

    public static List<Field> getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initValidateHandleMapping();
    }

    private void initValidateHandleMapping() {
        validateFieldActionHandleMapping.put(ValidateFieldAction.IF_EQ_NOT_NULL, applicationContext.getBean(IfEqNotNullHandleImpl.class));

    }

    /***
     * 封装字段信息
     * */
    public class ConditionalValidateFieldInfo {
        private String fieldName;
        private ConditionalValidateField conditionalValidateField;

        public ConditionalValidateFieldInfo(String fieldName, ConditionalValidateField conditionalValidateField) {
            this.fieldName = fieldName;
            this.conditionalValidateField = conditionalValidateField;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public ConditionalValidateField getConditionalValidateField() {
            return conditionalValidateField;
        }

        public void setConditionalValidateField(ConditionalValidateField conditionalValidateField) {
            this.conditionalValidateField = conditionalValidateField;
        }
    }

}

package com.springboot.sample.annotation;

import java.lang.annotation.*;


@Repeatable(Annotations.class)
public @interface ConditionalValidateField {

    /***
     * 关联字段
     * */
    String relationField();

    /***
     * 要执行的的校验动作
     * */
    int action();

    /** 该字段的值为**/
    String ifValue() default "";

    /***
     * 异常信息
     * */
    String message() default "";


}

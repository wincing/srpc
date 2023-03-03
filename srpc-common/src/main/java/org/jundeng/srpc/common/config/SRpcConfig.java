package org.jundeng.srpc.common.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记是否为配置类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface SRpcConfig {
    /**
     * 用于类时传入配置文件路径，用于属性通过传入字符串进行赋值
     */
    String value() default "";
}

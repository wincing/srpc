package org.jundeng.srpc.common.config.loader.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.jundeng.srpc.common.config.loader.ConfigLoader;
import org.jundeng.srpc.common.config.SRpcConfig;
import org.jundeng.srpc.common.util.StringUtil;

/**
 * 以annotation->properties->systemProperties的优先级读取配置
 * todo: 根据配置进行类加载
 * @param <T> 要加载Config类的类型
 */
public class DefaultConfigLoader<T> implements ConfigLoader<T> {
    /** 默认在srpc.properties下取 **/
    public static final String PROPERTIES_CONFIG_PATH = "srpc.properties";

    @Override
    public T loadConfig(String fileName, Class<T> clazz) {
        T configInstance = null;
        try {
            configInstance = clazz.newInstance();
            Setting setting = SettingUtil.get(StringUtil.isBlank(fileName) ? PROPERTIES_CONFIG_PATH : fileName);

            for (Field field : clazz.getDeclaredFields()) {
                String configValue = null;
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                // 从@SRpcConfig注解上读取值
                if (field.isAnnotationPresent(SRpcConfig.class)) {
                    configValue = field.getAnnotation(SRpcConfig.class).value();
                }
                // 注解未读取到则获取配置文件
                if (configValue == null) {
                    configValue = setting.getStr(field.getName());
                }
                // 最后如果还为null，则获取系统变量
                if (configValue == null) {
                    configValue = System.getProperty(field.getName());
                }
                // 将读取到的值设置到属性中
                if (configValue != null) {
                    injectField(field, configInstance, configValue);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return configInstance;
    }

    private void injectField(Field field, T instance, String configValue) {
        try {
            Object convertedValue = Convert.convert(field.getType(), configValue);
            field.setAccessible(true);
            field.set(instance, convertedValue);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

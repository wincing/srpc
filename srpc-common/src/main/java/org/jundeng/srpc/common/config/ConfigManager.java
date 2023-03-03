package org.jundeng.srpc.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jundeng.srpc.common.config.loader.ConfigLoader;
import org.jundeng.srpc.common.extension.ExtensionLoader;

public class ConfigManager {

    private static final ConfigManager configManager = new ConfigManager();

    /** 配置类加载器 **/
    private static ConfigLoader configLoader;

    /** 配置类缓存 {key:配置类类型} **/
    private final Map<Class<?>, Object> configCache = new ConcurrentHashMap<>();

    private static void setConfigLoader(String key) {
        configLoader = ExtensionLoader.getExtensionLoader(ConfigLoader.class).getExtension(key);
    }

    private ConfigManager() {
        setConfigLoader("");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T loadConfig(Class<T> clazz) {
        T config = (T) configCache.get(clazz);
        if (config == null) {
            config = loadAndCreateConfig(clazz);
            configCache.put(clazz, config);
        }
        return config;
    }

    /**
     * 加载并创建配置类
     * @param clazz 需要加载的配置类class
     */
    @SuppressWarnings("unchecked")
    private <T> T loadAndCreateConfig(Class<T> clazz) {
        SRpcConfig annotation = clazz.getAnnotation(SRpcConfig.class);
        if (annotation == null) {
            throw new IllegalStateException("Config class("+ clazz.getName() + ") without @SRpcConfig annotation!");
        }
        String fileName = annotation.value();
        return (T) configLoader.loadConfig(fileName, clazz);
    }

}

package org.jundeng.srpc.common.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jundeng.srpc.common.util.Holder;
import org.jundeng.srpc.common.util.StringUtil;

/**
 * 加载拓展插件
 * 每个拓展类加载器对应一个拓展类接口
 */
public class ExtensionLoader<T> {

    /** 扩展类存放的目录地址 **/
    private static final String EXTENSION_PATH = "META-INF/srpc/";

    /** 全局加载器缓存 {key: 拓展类接口, value: 负责加载该接口的loader} **/
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /** 全局实例缓存 {key: 拓展类, value: 拓展类对应实例} **/
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /** 扩展类实例缓存 {key: 拓展类名}*/
    private final Map<String, T> extensionsCache = new ConcurrentHashMap<>();

    /** 扩展类缓存 {key: 配置中的别名, value: 拓展类} **/
    private final Holder<Map<String, Class<?>>> classesCached = new Holder<>();

    /** 拓展接口 **/
    private final Class<?> type;

    /** 并发控制锁 **/
    private final Map<String, Object> lockMap = new ConcurrentHashMap<>();

    /** 默认拓展类名 **/
    private String cachedDefaultName;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        cachedDefaultName = type.getAnnotation(SRpcSPI.class).value();
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
           throw new IllegalStateException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalStateException("Extension type(" + type + ") is not interface!");
        }
        if (!type.isAnnotationPresent(SRpcSPI.class)) {
            throw new IllegalStateException("Type(" + type + ") without @SRpcSPI Annotation!");
        }

        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            loader = new ExtensionLoader<>(type);
            EXTENSION_LOADERS.putIfAbsent(type, loader);
        }
        return loader;
    }

    public T getExtension(String name) {
        if (StringUtil.isBlank(name)) {
            if (StringUtil.isBlank(cachedDefaultName)) {
                throw new IllegalStateException("Extension class is not configured!");
            } else {
                // 加载默认拓展类
                return getDefaultExtension();
            }
        }

        T instance = extensionsCache.get(name);
        if (instance == null) {
            Object lock = lockMap.computeIfAbsent(name, k -> new Object());
            synchronized (lock) {
                // 二次检查并创建拓展类
                instance = extensionsCache.computeIfAbsent(name, k -> createExtension(name));
            }
        }
        return instance;
    }


    public T getDefaultExtension() {
        return getExtension(cachedDefaultName);
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        // 获取当前类型所有扩展类并获取对应name的拓展类
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalStateException("Extension(" + name + ") not found class!");
        }

        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        try {
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            // todo: 依赖注入
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension(" + name + ") not found class! Detail: " + t.getMessage());
        }
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = classesCached.get();
        if (classes == null) {
            synchronized (classesCached) {
                classes = classesCached.get();
                if (classes == null) {
                    // 从配置文件中进行类加载
                    classes = loadClassesFromResources();
                    classesCached.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadClassesFromResources() {
        String fileName = EXTENSION_PATH + type.getName();
        Map<String, Class<?>> extensionClasses = new ConcurrentHashMap<>();
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(fileName);
            while (resources.hasMoreElements()) {
                URL resourceURL = resources.nextElement();
                // 解析配置文件
                loadResource(extensionClasses, classLoader, resourceURL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extensionClasses;
    }


    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            String line;
            String name = null, className = null;
            while ((line = reader.readLine()) != null) {
                // 忽略注释
                int note = line.indexOf('#');
                if (note >= 0) {
                    line = line.substring(0, note);
                }
                line = line.trim();

                // 读取拓展的别名与全类名
                if (line.length() > 0) {
                    int eq = line.indexOf('=');
                    name = line.substring(0, eq).trim();
                    className = line.substring(eq + 1).trim();
                }

                if (!StringUtil.isBlank(name) && !StringUtil.isBlank(className)) {
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        extensionClasses.put(name, clazz);

                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("Extension file parse fail: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Extension file parse fail: " + e.getMessage());
        }
    }
}

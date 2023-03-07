package org.jundeng.srpc.registry;

import org.jundeng.srpc.common.extension.ExtensionLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {

    public static Registry getRegistry() {
        return ExtensionLoader.getExtensionLoader(Registry.class).getDefaultExtension();
    }

    public static Registry getRegistry(String registryType) {
        return ExtensionLoader.getExtensionLoader(Registry.class).getExtension(registryType);
    }
}

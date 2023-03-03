package org.jundeng.srpc.common.config.loader;

import org.jundeng.srpc.common.extension.SRpcSPI;

/**
 * 从配置文件加载有关配置
 */
@SRpcSPI("default")
public interface ConfigLoader<T> {
    T loadConfig(String fileName, Class<T> clazz);
}

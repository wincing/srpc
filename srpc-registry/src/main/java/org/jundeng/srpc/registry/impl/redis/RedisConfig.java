package org.jundeng.srpc.registry.impl.redis;

import lombok.Data;
import org.jundeng.srpc.common.config.SRpcConfig;

/**
 * redis注册中心配置类
 */
@Data
@SRpcConfig("registry-config.properties")
public class RedisConfig {
    private String host;

    private Integer port;

    private String password;

    /** 服务更新时间间隔 单位: ms **/
    private Integer serviceTimeout;

    private String loadBalance;
}

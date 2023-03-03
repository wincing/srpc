package org.jundeng.srpc.loadbalance.common;

import lombok.Data;
import org.jundeng.srpc.common.util.StringUtil;

/**
 * 服务提供方的属性
 */
@Data
public class ServiceInfo {

    /** 权重 **/
    private Integer weight;

    /** 提供服务接口名 format: org.jundeng.srpc.registry.redis.RedisRegistry#doRegister **/
    private String interfaceName;

    /** 服务别名 **/
    private String alias;

    /** 服务地址 format: hostname:port **/
    private String url;

    public String solveHost() {
        if (StringUtil.isBlank(url)) {
            throw new IllegalStateException("Can not solve service host because url is null!");
        }
        int colon = url.indexOf(':');
        if (colon == -1) {
            throw new IllegalStateException("Url format error!");
        }
        return url.substring(0, colon);
    }

    public Integer solvePort() {
        if (StringUtil.isBlank(url)) {
            throw new IllegalStateException("Can not solve service port because url is null!");
        }
        int colon = url.indexOf(':');
        if (colon == -1) {
            throw new IllegalStateException("Url format error!");
        }
        return Integer.valueOf(url.substring(colon + 1));
    }
}

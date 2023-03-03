package org.jundeng.srpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import java.util.Set;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

public abstract class AbstractRegistry implements Registry {

    /** 注册中心类型 **/
    private String type;

    /** 注册服务集合 **/
    private final Set<ServiceInfo> registered = new ConcurrentHashSet<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<ServiceInfo> getRegistered() {
        return registered;
    }
}

package org.jundeng.srpc.registry;

import org.jundeng.srpc.common.extension.SRpcSPI;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

@SRpcSPI("redis")
public interface Registry {
    Boolean register(ServiceInfo serviceInfo);

    String getService(ServiceInfo serviceInfo);

    void unRegister(ServiceInfo serviceInfo);

}

package org.jundeng.srpc.loadbalance;

import java.util.List;
import org.jundeng.srpc.common.extension.SRpcSPI;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

@SRpcSPI("default")
public interface LoadBalance {
    /**
     * 通过负载均衡算法获取服务提供方的url
     * @param serviceInfoList
     */
    String select(List<ServiceInfo> serviceInfoList);
}

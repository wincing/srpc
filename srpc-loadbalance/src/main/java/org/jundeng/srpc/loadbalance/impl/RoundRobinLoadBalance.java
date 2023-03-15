package org.jundeng.srpc.loadbalance.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jundeng.srpc.loadbalance.AbstractLoadBalance;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

/**
 * 轮询负载均衡算法
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    /** 当前用的服务提供方下标 **/
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    protected int calculateWeight(ServiceInfo serviceInfo) {
        return serviceInfo.getWeight();
    }

    @Override
    public ServiceInfo doSelect(List<ServiceInfo> serviceInfoList) {
        int index = currentIndex.getAndIncrement() % serviceInfoList.size();
        return serviceInfoList.get(index);
    }
}

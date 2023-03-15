package org.jundeng.srpc.loadbalance.impl;

import org.jundeng.srpc.loadbalance.AbstractLoadBalance;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡算法
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected int calculateWeight(ServiceInfo serviceInfo) {
        return serviceInfo.getWeight();
    }

    @Override
    public ServiceInfo doSelect(List<ServiceInfo> serviceInfoList) {
        Random random = new Random();
        int idx = random.nextInt(serviceInfoList.size());
        return serviceInfoList.get(idx);
    }
}

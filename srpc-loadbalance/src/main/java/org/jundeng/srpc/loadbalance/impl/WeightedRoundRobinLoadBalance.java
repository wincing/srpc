package org.jundeng.srpc.loadbalance.impl;

import org.jundeng.srpc.loadbalance.AbstractLoadBalance;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 平滑加权轮询负载均衡算法
 */
public class WeightedRoundRobinLoadBalance extends AbstractLoadBalance {

    /** 存储动态权重 {key: 服务对应下标 value: 权重} **/
    private Map<Integer, Integer> indexWeightMap = null;

    /** 初始权重和 **/
    private int initialWeights;

    /** 上一次的服务列表 **/
    private List<ServiceInfo> lastServiceInfoList = null;

    @Override
    protected int calculateWeight(ServiceInfo serviceInfo) {
        return serviceInfo.getWeight();
    }

    @Override
    public ServiceInfo doSelect(List<ServiceInfo> serviceInfoList) {
        // 第一次调用初始化或服务列表发生更改
        if (!checkEquals(serviceInfoList)) {
            lastServiceInfoList = serviceInfoList;
            indexWeightMap = new ConcurrentHashMap<>();
            initialWeights = 0;

            for (int i = 0; i < serviceInfoList.size(); i++) {
                ServiceInfo serviceInfo = serviceInfoList.get(i);
                indexWeightMap.put(i, serviceInfo.getWeight());
                // 计算初始权重和
                initialWeights += serviceInfo.getWeight();
            }
        }

        // 寻找当前最大权重
        int maxCurrentWeight = - 1;
        int nowIndex = 0;
        for (Integer idx : indexWeightMap.keySet()) {
            Integer weight = indexWeightMap.get(idx);
            // System.out.print(serviceInfoList.get(idx).getUrl() + "->" + weight + " ");
            if (maxCurrentWeight < weight) { // 权重需要 > 1，因此不会出现未赋值的情况。
                maxCurrentWeight = weight;
                nowIndex = idx;
            }
        }
        // System.out.println();
        indexWeightMap.put(nowIndex, maxCurrentWeight - initialWeights);

        // 所有实例权重加上原始权重
        for (Map.Entry<Integer, Integer> entry : indexWeightMap.entrySet()) {
            int newWeight = entry.getValue() + lastServiceInfoList.get(entry.getKey()).getWeight();
            entry.setValue(newWeight);
        }

        return serviceInfoList.get(nowIndex);
    }

    /**
     * 判断服务实例列表是否发生变化，如果变化则需重新开始执行负载均衡算法
     */
    private boolean checkEquals(List<ServiceInfo> serviceInfoList) {
        if (lastServiceInfoList == null || serviceInfoList.size() != lastServiceInfoList.size()) {
            return false;
        }
        for (ServiceInfo serviceInfo : lastServiceInfoList) {
            if (!serviceInfoList.contains(serviceInfo)) {
                return false;
            }
        }
        return true;
    }

}

package org.jundeng.srpc.loadbalance;

import java.util.List;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String select(List<ServiceInfo> serviceInfoList) {
        if (serviceInfoList == null || serviceInfoList.size() == 0) {
            return null;
        }
        for (ServiceInfo serviceInfo : serviceInfoList) {
            serviceInfo.setWeight(calculateWeight(serviceInfo));
        }
        return doSelect(serviceInfoList).getUrl();
    }

    protected abstract int calculateWeight(ServiceInfo serviceInfo);

    public abstract ServiceInfo doSelect(List<ServiceInfo> serviceInfoList);
}

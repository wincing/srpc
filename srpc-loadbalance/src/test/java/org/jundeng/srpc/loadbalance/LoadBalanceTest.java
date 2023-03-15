package org.jundeng.srpc.loadbalance;

import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoadBalanceTest {

    private List<ServiceInfo> createServcieInfoList() {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        ServiceInfo a = new ServiceInfo();
        a.setWeight(5);
        a.setUrl("A");

        ServiceInfo b = new ServiceInfo();
        b.setWeight(1);
        b.setUrl("B");

        ServiceInfo c = new ServiceInfo();
        c.setWeight(1);
        c.setUrl("C");

        serviceInfoList.add(a);
        serviceInfoList.add(b);
        serviceInfoList.add(c);

        return serviceInfoList;
    }

    @Test
    public void randomTest() {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("random");

        for (int i = 0; i < 10; i++) {
            String selectedService = loadBalance.select(createServcieInfoList());
            System.out.println(selectedService);
        }
    }

    @Test
    public void roundRobinTest() {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("roundRobin");

        for (int i = 0; i < 10; i++) {
            String selectedService = loadBalance.select(createServcieInfoList());
            System.out.println(selectedService);
        }
    }

    @Test
    public void weightedRoundRobinTest() {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("smoothWeight");

        for (int i = 0; i < 10; i++) {
            String selectedService = loadBalance.select(createServcieInfoList());
            System.out.println(selectedService);
        }
    }
}

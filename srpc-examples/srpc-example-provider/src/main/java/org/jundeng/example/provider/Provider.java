package org.jundeng.example.provider;

import org.jundeng.example.provider.service.HelloService;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.network.server.RpcServerSocket;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.jundeng.srpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Provider {
    private static final Logger logger = LoggerFactory.getLogger(Provider.class);

    public static void main(String[] args) throws InterruptedException {
        // 启动监听
        RpcServerSocket rpcServerSocket = new RpcServerSocket();
        new Thread(rpcServerSocket).start();

        // 注册服务
        Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("redis");
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setInterfaceName(HelloService.class.getName());

        String serviceUrl = null;
        for (int i = 0; i < 10; i++) {
            if (serviceUrl != null) {
                logger.info("ServiceUrl = " + serviceUrl);
                break;
            }
            Thread.sleep(500);
            serviceUrl = rpcServerSocket.getServiceUrl();
        }
        serviceInfo.setUrl(serviceUrl);
        registry.register(serviceInfo);
    }
}

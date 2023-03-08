package org.jundeng.example.consumer;

import org.jundeng.example.provider.service.HelloService;
import org.jundeng.srpc.core.reflect.invoke.RpcProxy;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = RpcProxy.invoke(HelloService.class);
        System.out.println(helloService.sayHello());
    }
}

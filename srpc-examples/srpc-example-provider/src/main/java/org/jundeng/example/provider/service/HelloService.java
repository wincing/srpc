package org.jundeng.example.provider.service;

import org.jundeng.srpc.common.extension.SRpcSPI;

@SRpcSPI("default")
public interface HelloService {
    String sayHello();
}

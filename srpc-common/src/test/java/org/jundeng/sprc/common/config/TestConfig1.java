package org.jundeng.sprc.common.config;

import org.jundeng.srpc.common.config.SRpcConfig;

@SRpcConfig
public class TestConfig1 {
    public int port;
    private String name;

    @Override
    public String toString() {
        return "TestConfig1{" + "port=" + port + ", name='" + name + '\'' + '}';
    }
}

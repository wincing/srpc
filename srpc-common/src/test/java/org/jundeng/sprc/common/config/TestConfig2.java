package org.jundeng.sprc.common.config;

import org.jundeng.srpc.common.config.SRpcConfig;

@SRpcConfig("test_annotation.properties")
public class TestConfig2 {
    @SRpcConfig("6666")
    public int port;
    private String name;

    @Override
    public String toString() {
        return "TestConfig1{" + "port=" + port + ", name='" + name + '\'' + '}';
    }
}

package org.jundeng.srpc.rpc.network;

import org.jundeng.srpc.core.network.server.RpcServerSocket;

public class TestServer {
    public static void main(String[] args) {
        new Thread(new RpcServerSocket()).start();
    }
}

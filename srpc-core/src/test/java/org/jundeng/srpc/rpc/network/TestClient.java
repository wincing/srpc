package org.jundeng.srpc.rpc.network;

import io.netty.channel.ChannelFuture;
import org.jundeng.srpc.core.network.client.RpcClientSocket;
import org.jundeng.srpc.core.network.message.Request;

public class TestClient {
    public static void main(String[] args) {
        Request request = new Request();
        request.setPort(49152);
        request.setHost("localhost");

        RpcClientSocket client = new RpcClientSocket(request);
    }
}

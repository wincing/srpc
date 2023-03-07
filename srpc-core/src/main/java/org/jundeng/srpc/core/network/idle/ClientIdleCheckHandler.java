package org.jundeng.srpc.core.network.idle;

import io.netty.handler.timeout.IdleStateHandler;

public class ClientIdleCheckHandler extends IdleStateHandler {
    public ClientIdleCheckHandler() {
        // 5s未发送写事件发送心跳包
        super(0, 5, 0);
    }
}

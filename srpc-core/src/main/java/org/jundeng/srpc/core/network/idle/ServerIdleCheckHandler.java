package org.jundeng.srpc.core.network.idle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerIdleCheckHandler extends IdleStateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerIdleCheckHandler.class);

    public ServerIdleCheckHandler() {
        // 10s内未收到数据包，断开连接
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (IdleStateEvent.READER_IDLE_STATE_EVENT.equals(evt)) {
            logger.info(" No reading for more than 10 seconds, connection closed!");
            ctx.close();
        }
        super.channelIdle(ctx, evt);
    }
}

package org.jundeng.srpc.core.network.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Response;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;
import org.jundeng.srpc.core.network.sync.SyncWriteMap;
import org.jundeng.srpc.core.network.sync.WriteFuture;
import org.jundeng.srpc.core.serializer.SerializeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * client端处理器
 */
public class RpcClientSocketHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientSocketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        long streamId = response.getStreamId();
        WriteFuture<Response> future = SyncWriteMap.CLIENT_RESPONSE_MAP.get(streamId);
        if (future != null) {
            future.setResult(response);
        }
    }

    /**
     * 发送心跳包
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.WRITER_IDLE_STATE_EVENT.equals(evt)) {
            logger.info("Write idle happen, send heartbeat message!");
            SRpcMessage srpcMessage = new SRpcMessage();
            SRpcMessageHeader srpcMessageHeader = new SRpcMessageHeader();

            srpcMessageHeader.setEventType(MessageConstants.EVENT_HEARTBEAT);
            srpcMessageHeader.setCompressId(CompressType.DEFAULT.getValue());  // todo: 此处写死，应通过配置文件配置
            srpcMessageHeader.setSerializeId(SerializeType.PROTOSTUFF.getValue());
            srpcMessage.setHeader(srpcMessageHeader);

            ctx.writeAndFlush(srpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(cause.getMessage());
    }
}

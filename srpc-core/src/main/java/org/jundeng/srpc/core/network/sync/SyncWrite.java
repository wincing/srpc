package org.jundeng.srpc.core.network.sync;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.Response;

/**
 * 执行同步写操作
 */
public class SyncWrite {
    /**
     *
     * @param channel 通信channel
     * @param request 待发送的请求
     * @param timeout 超时时间，单位秒
     */
    public static Response writeAndSync(final Channel channel, final Request request, long timeout) throws Exception {
        if (channel == null) {
            throw new NullPointerException("Channel can not be null!");
        }
        if (request == null) {
            throw new NullPointerException("Request to be send is null!");
        }
        if (timeout <= 0) {
            // 服务端断开连接时间为10s，超过10s必然无法获取
            timeout = 10L;
        }

        request.setStreamId(MessageConstants.STREAM_ID.incrementAndGet());
        SyncWriteFuture writeFuture = new SyncWriteFuture(request.getStreamId(), timeout);
        SyncWriteMap.CLIENT_RESPONSE_MAP.put(request.getStreamId(), writeFuture);

        return doWriteAndSync(channel, request, timeout, writeFuture);
    }

    private static Response doWriteAndSync(final Channel channel, final Request request, final long timeout,
        final WriteFuture<Response> writeFuture) throws Exception {


        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                writeFuture.setIsSuccess(future.isSuccess());
                writeFuture.setCause(future.cause());
                if (!future.isSuccess()) {
                    SyncWriteMap.CLIENT_RESPONSE_MAP.remove(request.getStreamId());
                }
            }
        });

        Response response = writeFuture.get(timeout, TimeUnit.SECONDS); // 同步获取，等待时间timeout
        if (response == null) {
            if (writeFuture.isTimeout()) {
                throw new TimeoutException("Timeout while waiting for write result!");
            } else {
                // write exception
                throw new Exception(writeFuture.getCause());
            }
        }
        return response;
    }
}

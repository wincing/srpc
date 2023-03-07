package org.jundeng.srpc.core.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;

/**
 * 将SRpcMessage转为Request
 */
public class RequestDecoder extends MessageToMessageDecoder<SRpcMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, SRpcMessage msg, List<Object> out) throws Exception {
        SRpcMessageHeader header = msg.getHeader();
        // 如果不是request一定是heartbeat，因为服务端不会收到response，直接忽略
        if (header.getEventType() == MessageConstants.EVENT_REQUEST) {
            Request request = (Request) msg.getMessageBody();
            out.add(request);
        }
    }
}

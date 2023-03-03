package org.jundeng.srpc.core.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
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
        byte eventType = header.getEventType();
        Object messageBody = msg.getMessageBody();

        if (messageBody instanceof Request) {
            Request request = new Request();

        } else {

        }
    }
}

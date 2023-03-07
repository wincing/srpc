package org.jundeng.srpc.core.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Response;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;

public class ResponseDecoder extends MessageToMessageDecoder<SRpcMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, SRpcMessage msg, List<Object> out) throws Exception {
        SRpcMessageHeader header = msg.getHeader();
        if (header.getEventType() == MessageConstants.EVENT_RESPONSE) {
            Response response = (Response) msg.getMessageBody();
            out.add(response);
        }
    }
}

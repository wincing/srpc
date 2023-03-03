package org.jundeng.srpc.core.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Response;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;

/**
 * 将Response转为SRpcMessage
 */
public class ResponseEncoder extends MessageToMessageEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, List<Object> out) throws Exception {
        SRpcMessage srpcMessage = new SRpcMessage();
        SRpcMessageHeader srpcMessageHeader = new SRpcMessageHeader();

        // 填充消息头，此处长度还未被计算，不需要传，
        srpcMessageHeader.setEventType(MessageConstants.EVENT_REQUEST);
        srpcMessageHeader.setSerializeId(response.getSerializeId());
        srpcMessageHeader.setCompressId(response.getCompressId());
        srpcMessageHeader.setStreamId(response.getStreamId());

        srpcMessage.setMessageBody(response);
        srpcMessage.setHeader(srpcMessageHeader);
        out.add(srpcMessage);
    }
}

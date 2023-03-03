package org.jundeng.srpc.core.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;
import org.jundeng.srpc.core.serializer.SerializeType;

/**
 * 将Request转为SRpcMessage
 */
public class RequestEncoder extends MessageToMessageEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Request req, List<Object> out) throws Exception {
        SRpcMessage srpcMessage = new SRpcMessage();
        SRpcMessageHeader srpcMessageHeader = new SRpcMessageHeader();

        // 填充消息头，此处长度还未被计算，不需要传，
        srpcMessageHeader.setEventType(MessageConstants.EVENT_REQUEST);
        // todo: 改为不写死，通过配置文件配置
        srpcMessageHeader.setCompressId(CompressType.DEFAULT.getValue());
        srpcMessageHeader.setSerializeId(SerializeType.PROTOSTUFF.getValue());
        srpcMessageHeader.setStreamId(MessageConstants.STREAM_ID.incrementAndGet());

        srpcMessage.setMessageBody(req);
        srpcMessage.setHeader(srpcMessageHeader);
        out.add(srpcMessage);
    }
}

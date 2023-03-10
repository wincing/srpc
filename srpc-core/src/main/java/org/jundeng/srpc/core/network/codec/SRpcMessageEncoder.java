package org.jundeng.srpc.core.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.compress.Compressor;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;
import org.jundeng.srpc.core.serializer.SerializeType;
import org.jundeng.srpc.core.serializer.Serializer;

/**
 * 编码srpc协议报文
 */
public class SRpcMessageEncoder extends MessageToByteEncoder<SRpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, SRpcMessage msg, ByteBuf out) {
        SRpcMessageHeader header = msg.getHeader();

        out.writeByte(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getEventType());
        out.writeByte(header.getSerializeId());
        out.writeByte(header.getCompressId());
        out.writeLong(header.getStreamId());
        out.writerIndex(out.writerIndex() + MessageConstants.LENGTH_FILED_LENGTH);

        int bodyLength = writeBody(msg, out);
        // 记录写指针位置
        int writerIndex = out.writerIndex();
        // 回填消息体长度
        out.writerIndex(out.writerIndex() - bodyLength - MessageConstants.LENGTH_FILED_LENGTH);
        out.writeInt(MessageConstants.MESSAGE_HEADER_LENGTH + bodyLength);
        // 恢复写指针
        out.writerIndex(writerIndex);
    }

    private int writeBody(SRpcMessage msg, ByteBuf out) {
        SRpcMessageHeader header = msg.getHeader();
        // 心跳包无消息体直接返回
        if (header.getEventType() == MessageConstants.EVENT_HEARTBEAT) {
            return 0;
        }

        // 获取serializer
        Serializer serializer;
        try {
            SerializeType serializeType = SerializeType.getSerializeType(header.getSerializeId());
            serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializeType.getName());
        } catch (Exception e) {
            throw new RuntimeException("Unsupportable serialize type, detail: " + e.getMessage());
        }

        // 获取compressor
        Compressor compressor;
        try {
            CompressType compressType = CompressType.getCompressType(header.getCompressId());
            compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getDefaultExtension();
        } catch (Exception e) {
            throw new RuntimeException("Unsupportable compress type: detail: " + e.getMessage());
        }

        // 先序列化再压缩
        Object messageBody = msg.getMessageBody();
        byte[] bytes = compressor.compress(serializer.serialize(messageBody));
        out.writeBytes(bytes);
        return bytes.length;
    }
}

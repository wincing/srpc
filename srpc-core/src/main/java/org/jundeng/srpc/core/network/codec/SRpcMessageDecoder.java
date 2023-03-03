package org.jundeng.srpc.core.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.compress.Compressor;
import org.jundeng.srpc.core.network.message.MessageConstants;
import org.jundeng.srpc.core.network.message.SRpcMessage;
import org.jundeng.srpc.core.network.message.SRpcMessageHeader;
import org.jundeng.srpc.core.serializer.SerializeType;
import org.jundeng.srpc.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解码srpc协议报文
 */
public class SRpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(SRpcMessageDecoder.class);

    public SRpcMessageDecoder() {
        super(
            MessageConstants.MAX_FRAME_LENGTH,
            MessageConstants.MESSAGE_HEADER_LENGTH - MessageConstants.LENGTH_FILED_LENGTH,
            MessageConstants.LENGTH_FILED_LENGTH,
            -MessageConstants.MESSAGE_HEADER_LENGTH, // 后续消息长度 =  总长度(Offset + length读到的长度) - 消息头(lengthAdjustment)
            0);

    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object byteBuf = super.decode(ctx, in);
        if (byteBuf instanceof ByteBuf) {
            ByteBuf msg = (ByteBuf) byteBuf;
            if (msg.readableBytes() >= MessageConstants.MESSAGE_HEADER_LENGTH) {
                return decodeProtocol(msg);
            }
        }
        return byteBuf;
    }

    /**
     * 根据协议字段解码，严格遵循顺序
     */
    private SRpcMessage decodeProtocol(ByteBuf msg) {
        // 获取字段并构建SRpcMessage
        byte magic = msg.readByte();
        checkMagic(magic);

        byte version = msg.readByte();
        checkVersion(version);

        byte eventType = msg.readByte();
        byte serializeId = msg.readByte();
        byte compressId = msg.readByte();
        long streamId = msg.readLong();
        int messageLength = msg.readInt();

        SRpcMessageHeader srpcMessageHeader = new SRpcMessageHeader();

        srpcMessageHeader.setMagic(magic);
        srpcMessageHeader.setVersion(version);
        srpcMessageHeader.setEventType(eventType);
        srpcMessageHeader.setSerializeId(serializeId);
        srpcMessageHeader.setCompressId(compressId);
        srpcMessageHeader.setStreamId(streamId);
        srpcMessageHeader.setLength(messageLength); // todo: 报文长度貌似没有意义，待删除

        SRpcMessage srpcMessage = new SRpcMessage();
        srpcMessage.setHeader(srpcMessageHeader);

        int bodyLength = messageLength - MessageConstants.MESSAGE_HEADER_LENGTH;
        if (bodyLength == 0 || eventType == MessageConstants.EVENT_HEARTBEAT) {
            return  srpcMessage;
        }

        CompressType compressType = CompressType.getCompressType(compressId);
        if (compressType == null) {
            throw new IllegalArgumentException("Unsupportable compress type: id=" + compressId);
        }

        SerializeType serializeType = SerializeType.getSerializeType(serializeId);
        if (serializeType == null) {
            throw new IllegalArgumentException("Unsupportable serialize type: id=" + serializeId);
        }

        byte[] messageBody = new byte[bodyLength];
        msg.readBytes(messageBody);

        // 先解压再反序列化
        Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(compressType.getName());
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializeType.getName());
        srpcMessage.setMessageBody(serializer.serialize(compressor.compress(messageBody)));

        return srpcMessage;
    }

    private void checkVersion(byte version) {
        if (version != MessageConstants.SRPC_VERSION) {
            throw new IllegalArgumentException("Message with unknown version: " + version);
        }
    }

    private void checkMagic(byte magic){
        if (magic != MessageConstants.MAGIC) {
            throw new IllegalArgumentException("Message with unknown magic: " + magic);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.warn(cause.getMessage());
    }
}

package org.jundeng.srpc.core.network.message;

import lombok.Data;

/**
 * srpc通信协议消息头
 *
 * <pre>
 * magic（魔数） 1B
 * version（版本） 1B
 * eventType（事件类型: 0为请求，1为响应包, 2为心跳） 1B
 * serializeId（序列化id） 1B
 * compressId（压缩协议id） 1B
 * streamId（通信id） 8B
 * length（消息长度） 4B
 * </pre>
 */
@Data
public class SRpcMessageHeader {

    private byte magic = MessageConstants.MAGIC;
    private byte version = MessageConstants.SRPC_VERSION;
    private byte eventType;
    private byte serializeId;
    private byte compressId;
    private long streamId;
    private int length;
}

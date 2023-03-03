package org.jundeng.srpc.core.network.message;

import java.util.concurrent.atomic.AtomicLong;

public class MessageConstants {

    public static final byte MAGIC = (byte) 0x6874;

    public static final byte SRPC_VERSION = 1;

    /**
     * 数据包最大长度
     */
    public static final int MAX_FRAME_LENGTH = 4 * 1024 * 1024;

    public static final int MAGIC_LENGTH = 1;

    public static final int VERSION_LENGTH = 1;

    public static final int EVENT_TYPE_LENGTH = 1;

    public static final int SERIALIZE_ID_LENGTH = 1;

    public static final int COMPRESS_ID_LENGTH = 1;

    public static final int STREAM_ID_LENGTH = 4;

    /**
     * 总长度字段的长度
     */
    public static final int LENGTH_FILED_LENGTH = 2;

    /**
     * 消息头长度
     */
    public static final int MESSAGE_HEADER_LENGTH = MAGIC_LENGTH + VERSION_LENGTH + EVENT_TYPE_LENGTH + SERIALIZE_ID_LENGTH +
        COMPRESS_ID_LENGTH + STREAM_ID_LENGTH + LENGTH_FILED_LENGTH;
    /**
     * 自增streamId
     */
    public static final AtomicLong STREAM_ID = new AtomicLong(0);

    public static final byte EVENT_HEARTBEAT = 2;
    public static final byte EVENT_REQUEST = 0;
    public static final byte EVENT_RESPONSE = 1;
}

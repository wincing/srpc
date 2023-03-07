package org.jundeng.srpc.core.network.message;

import lombok.Data;

@Data
public class Response {

    private byte serializeId;

    private byte compressId;

    private long streamId;

    private Object result;

    private Object exceptionInfo;
}

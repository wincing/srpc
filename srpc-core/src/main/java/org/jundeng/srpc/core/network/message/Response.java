package org.jundeng.srpc.core.network.message;

import lombok.Data;

@Data
public class Response<T> {

    private byte serializeId;

    private byte compressId;

    private long streamId;

    private T result;

    private Object exceptionInfo;
}

package org.jundeng.srpc.core.network.message;

import lombok.Data;

@Data
public class Request {
    private long streamId;

    private String interfaceName;

    private String methodName;

    private String host;

    private Integer port;
}

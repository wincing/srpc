package org.jundeng.srpc.core.network.message;

import lombok.Data;

@Data
public class SRpcMessage {
    private SRpcMessageHeader header;

    private Object messageBody;
}

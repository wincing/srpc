package org.jundeng.srpc.core.network.message;

import java.util.List;
import lombok.Data;

@Data
public class Request {
    /** 标识一次通信过程 **/
    private long streamId;

    private byte serializeId;

    private byte compressId;

    /** 接口名 **/
    private String interfaceName;

    /** 要调用方法名 **/
    private String methodName;

    /** 入参 **/
    private Object[] args;

    /** 入参类型 **/
    private List<String> paramTypes;

    /** 主机名 **/
    private String host;

    /** 端口号 **/
    private Integer port;
}

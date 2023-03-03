package org.jundeng.srpc.core.serializer;

import org.jundeng.srpc.common.extension.SRpcSPI;

@SRpcSPI("protostuff")
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}

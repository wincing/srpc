package org.jundeng.srpc.core.serializer.impl;

import com.alibaba.fastjson.JSON;
import org.jundeng.srpc.core.serializer.Serializer;

public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }
}

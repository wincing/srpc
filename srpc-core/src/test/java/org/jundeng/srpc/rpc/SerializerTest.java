package org.jundeng.srpc.rpc;

import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.serializer.Serializer;
import org.jundeng.srpc.rpc.entity.EntityObject;
import org.jundeng.srpc.core.serializer.impl.JsonSerializer;
import org.jundeng.srpc.core.serializer.impl.ProtostuffSerializer;
import org.junit.Test;

public class SerializerTest {

    @Test
    public void testJson() {
        EntityObject obj = new EntityObject();
        JsonSerializer jsonSerializer = new JsonSerializer();
        byte[] bytes = jsonSerializer.serialize(obj);
        obj = jsonSerializer.deserialize(bytes, obj.getClass());
        System.out.println(obj);
    }

    @Test
    public void testProtostuff() {
        EntityObject obj = new EntityObject();
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        byte[] bytes = protostuffSerializer.serialize(obj);
        obj = protostuffSerializer.deserialize(bytes, obj.getClass());
        System.out.println(obj);
    }

    @Test
    public void loadProtostuff() {
        Serializer protostuff = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("protostuff");
    }

}

package org.jundeng.srpc.rpc;

import org.jundeng.srpc.core.compress.impl.DefaultCompressor;
import org.jundeng.srpc.core.compress.impl.GzipCompressor;
import org.jundeng.srpc.core.serializer.impl.JsonSerializer;
import org.jundeng.srpc.rpc.entity.EntityObject;
import org.junit.Test;

public class CompressorTest {

    @Test
    public void testDefault() {
        EntityObject obj = new EntityObject();
        DefaultCompressor defaultCompressor = new DefaultCompressor();

        JsonSerializer jsonSerializer = new JsonSerializer();
        byte[] bytes = jsonSerializer.serialize(obj);

        byte[] compressed = defaultCompressor.compress(bytes);
        byte[] decompressed = defaultCompressor.decompress(compressed);
        System.out.println(jsonSerializer.deserialize(decompressed, EntityObject.class));
    }

    @Test
    public void testGzip() {
        EntityObject obj = new EntityObject();
        GzipCompressor gzipCompressor = new GzipCompressor();

        JsonSerializer jsonSerializer = new JsonSerializer();
        byte[] bytes = jsonSerializer.serialize(obj);

        byte[] compressed = gzipCompressor.compress(bytes);
        byte[] decompressed = gzipCompressor.decompress(compressed);
        System.out.println(jsonSerializer.deserialize(decompressed, EntityObject.class));
    }
}

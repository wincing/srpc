package org.jundeng.srpc.core.compress.impl;

import org.jundeng.srpc.core.compress.Compressor;

public class DefaultCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }
}

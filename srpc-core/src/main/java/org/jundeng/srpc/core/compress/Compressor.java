package org.jundeng.srpc.core.compress;

import org.jundeng.srpc.common.extension.SRpcSPI;

@SRpcSPI("default")
public interface Compressor {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}

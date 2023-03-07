package org.jundeng.srpc.core.network.sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jundeng.srpc.core.network.message.Response;

public class SyncWriteMap {
    /**
     *
     */
    public static final Map<Long, WriteFuture<Response>> CLIENT_RESPONSE_MAP = new ConcurrentHashMap<>();
}

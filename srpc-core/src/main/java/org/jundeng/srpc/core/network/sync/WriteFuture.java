package org.jundeng.srpc.core.network.sync;

import java.util.concurrent.Future;

public interface WriteFuture<T> extends Future<T> {
    Throwable getCause();

    void setCause(Throwable cause);

    boolean isWriteSuccess();

    void setIsSuccess(boolean successFlag);

    long getStreamId();

    T getResult();

    void setResult(T result);

    boolean isTimeout();
}

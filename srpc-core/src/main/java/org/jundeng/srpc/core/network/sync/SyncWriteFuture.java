package org.jundeng.srpc.core.network.sync;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jundeng.srpc.core.network.message.Response;

/**
 * 同步写控制对象
 */
public class SyncWriteFuture implements WriteFuture<Response> {

    /**
     * 同步结果
     */
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 开始时间
     */
    private final long begin = System.currentTimeMillis();

    private long timeout;

    private Response response;

    private final long streamId;

    private boolean successFlag;

    private Throwable cause;

    public SyncWriteFuture(long streamId) {
        this.streamId = streamId;
    }

    public SyncWriteFuture(long streamId, long timeout) {
        this.streamId = streamId;
        this.timeout = timeout;
        successFlag = true;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public boolean isWriteSuccess() {
        return this.successFlag;
    }

    @Override
    public void setIsSuccess(boolean successFlag) {
        this.successFlag = successFlag;
    }

    @Override
    public long getStreamId() {
        return  streamId;
    }

    @Override
    public Response getResult() {
        return this.response;
    }

    @Override
    public void setResult(Response result) {
        this.response = result;
        // 解除阻塞
        latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Response get() throws InterruptedException {
        // 获取值时进行阻塞，直到setResult()被调用
        latch.await();
        return response;
    }

    @Override
    @Deprecated
    public Response get(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch.await(timeout, unit)) {
            return response;
        }
        return null;
    }

    @Override
    public boolean isTimeout() {
        return System.currentTimeMillis() - begin > timeout;
    }
}

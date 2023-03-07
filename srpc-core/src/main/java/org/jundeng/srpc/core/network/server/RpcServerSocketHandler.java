package org.jundeng.srpc.core.network.server;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerSocketHandler extends SimpleChannelInboundHandler<Request> {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerSocketHandler.class);

    /** 执行本地调用任务线程池 **/
    private static final ExecutorService localInvokeThreadPool = Executors.newFixedThreadPool(10);

    /** 缓存class **/
    private static final HashMap<String, Class> classCache = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {

        Class<?> targetClazz = classCache.get(request.getInterfaceName());
        if (targetClazz == null) {
            targetClazz = Class.forName(request.getInterfaceName());
        }

        List<String> paramTypes = request.getParamTypes();
        Class[] paramsClazz = new Class[paramTypes.size()];
        if (!CollectionUtil.isEmpty(paramTypes)) {
            for (int i = 0; i < paramTypes.size(); i++) {
                String paramTypeName = paramTypes.get(i);
                paramsClazz[i] = classCache.get(paramTypeName);
                if (paramsClazz[i] == null) {
                    Class.forName(paramTypes.get(i));
                }
            }
        }

        // 反射调用
        Method targetMethod = targetClazz.getMethod(request.getMethodName(), paramsClazz);
        Object targetObj = targetClazz.newInstance();

        localInvokeThreadPool.submit(() -> asyncLocalInvoke(ctx, request, targetMethod, targetObj));
    }

    /**
     * 异步执行本地方法并返回response
     */
    private void asyncLocalInvoke(ChannelHandlerContext ctx, Request request, Method targetMethod, Object targetObj) {

        Object result = null;
        Response response = new Response();

        try {
            result = targetMethod.invoke(targetObj, request.getArgs());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage());
            response.setExceptionInfo(e.getMessage());
        }
        response.setStreamId(request.getStreamId());
        response.setSerializeId(request.getSerializeId());
        response.setCompressId(request.getCompressId());
        response.setResult(result);

        ctx.writeAndFlush(response);
    }
}

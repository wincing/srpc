package org.jundeng.srpc.core.reflect.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.network.client.RpcClientSocket;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.Response;
import org.jundeng.srpc.core.network.sync.SyncWrite;
import org.jundeng.srpc.core.serializer.SerializeType;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.jundeng.srpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private static Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("redis");

    /** 客户端channel缓存， key: hostname:port **/
    private static final Map<String, Channel> channelCache = new ConcurrentHashMap<>();

    private static  ExecutorService channelAttainedExecutorService = Executors.newFixedThreadPool(10);

    private String interfaceName;

    RpcInvocationHandler(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    private String findService() {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setInterfaceName(this.interfaceName);
        return registry.getService(serviceInfo);
    }

    private Channel connect(Request request, String serviceUrl) throws InterruptedException {

        // 查询channel缓存
        Channel channel;
        channel = channelCache.get(serviceUrl);
        if (channel != null) {
            if (channel.isOpen()) {
                return channel;
            } else {
                channelCache.remove(serviceUrl);
            }
        }

        RpcClientSocket clientSocket = new RpcClientSocket(request);
        channelAttainedExecutorService.submit(clientSocket);

        // 每隔0.5s尝试获取channel
        for (int i = 0; i < 10; i++) {
            if (channel != null) {
                break;
            }
            Thread.sleep(500);
            channel = clientSocket.getChannel();
        }
        return channel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取需要访问的Service Provider地址
        String serviceUrl = findService();
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setUrl(serviceUrl);

        // 构建请求包
        Request request = new Request();
        request.setCompressId(CompressType.DEFAULT.getValue()); // todo: 此处写死，应通过配置文件配置
        request.setSerializeId(SerializeType.PROTOSTUFF.getValue());
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setHost(serviceInfo.solveHost());
        request.setPort(serviceInfo.solvePort());

        // 获取连接
        Channel channel = connect(request, serviceUrl);
        Response response = SyncWrite.writeAndSync(channel, request, 10L);

        return response.getResult();
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}

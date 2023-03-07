package org.jundeng.srpc.core.reflect.invoke;

import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.core.compress.CompressType;
import org.jundeng.srpc.core.network.message.Request;
import org.jundeng.srpc.core.network.message.Response;
import org.jundeng.srpc.core.network.sync.SyncWrite;
import org.jundeng.srpc.core.serializer.SerializeType;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.jundeng.srpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DeprecatedRpcInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeprecatedRpcInvocationHandler.class);


    /** 客户端channel缓存， key: hostname:port **/
    private static final Map<String, List<Channel>> channelCache = new ConcurrentHashMap<>();

    private static Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("redis");

    private String interfaceName;

    DeprecatedRpcInvocationHandler(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    private String findService() {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setInterfaceName(this.interfaceName);
        return registry.getService(serviceInfo);
    }

    private Channel connect(Request request, String serviceUrl) {
        List<Channel> channels = channelCache.computeIfAbsent(serviceUrl, k -> new Vector<>());

        // 看对应服务端是否有可用的channel
        Channel clientChannel = null;
        for (Channel channel : channels) {
            if (channel.isOpen()) {
            } else {
            }
        }

        // 执行ClientSocket.connect并获取返回的channel
        if (clientChannel == null) {
//            RpcClientSocket clientSocket = new RpcClientSocket(request);
//            FutureTask<Channel> futureTask = new FutureTask<>(clientSocket);
//            try {
//                new Thread(futureTask).start();
//                clientChannel = futureTask.get(5, TimeUnit.SECONDS);
//                channels.add(clientChannel);
//            } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                logger.error("Fail to create channel: " + e.getMessage());
//                return null;
//            }
        }
        return clientChannel;
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

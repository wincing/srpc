package org.jundeng.srpc.core.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.concurrent.Callable;
import org.jundeng.srpc.core.network.codec.RequestEncoder;
import org.jundeng.srpc.core.network.codec.ResponseDecoder;
import org.jundeng.srpc.core.network.codec.SRpcMessageDecoder;
import org.jundeng.srpc.core.network.codec.SRpcMessageEncoder;
import org.jundeng.srpc.core.network.idle.ClientIdleCheckHandler;
import org.jundeng.srpc.core.network.message.Request;

/**
 * 客户端：发送Request，接收Response
 */
public class RpcClientSocket implements Runnable {

    private static final Bootstrap bootstrap = new Bootstrap();

    private Request request;

    private final int port;

    private final String host;

    private Channel channel;

    public RpcClientSocket(Request request) {
        this.request = request;
        this.port = request.getPort();
        this.host = request.getHost();
    }

    @Override
    public void run() {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1); // 对于每次调用客户端只需读写一个channel
        bootstrap
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                    pipeline.addLast("srpcMessageDecoder", new SRpcMessageDecoder());
                    pipeline.addLast("message2ResponseDecoder", new ResponseDecoder());

                    pipeline.addLast("srpcMessageEncoder", new SRpcMessageEncoder());
                    pipeline.addLast("request2MessageEncoder", new RequestEncoder());

                    pipeline.addLast("idleChecker", new ClientIdleCheckHandler()); // 顺序不能变，保证idleChecker触发的事件能被后继处理器捕获
                    pipeline.addLast("clientRpcHandler", new RpcClientSocketHandler()); // 发送的心跳包是srpcMessage
                }
            });
        try {
            ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
            channel = future.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Channel getChannel() {
        return channel;
    }
}

package org.jundeng.srpc.core.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import org.jundeng.srpc.common.Constants;
import org.jundeng.srpc.core.network.codec.RequestDecoder;
import org.jundeng.srpc.core.network.codec.ResponseEncoder;
import org.jundeng.srpc.core.network.codec.SRpcMessageDecoder;
import org.jundeng.srpc.core.network.codec.SRpcMessageEncoder;
import org.jundeng.srpc.core.network.idle.ServerIdleCheckHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端：发送Response，接收Request
 */
public class RpcServerSocket implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerSocket.class);

    private ChannelFuture channelFuture;

    private int port;

    @Override
    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); // 只需1个线程监听请求即可
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                    pipeline.addLast("srpcMessageDecoder", new SRpcMessageDecoder());
                    pipeline.addLast("message2RequestDecoder", new RequestDecoder());

                    pipeline.addLast("srpcMessageEncoder", new SRpcMessageEncoder());
                    pipeline.addLast("response2MessageEncoder", new ResponseEncoder());

                    pipeline.addLast("idleChecker", new ServerIdleCheckHandler());
                    pipeline.addLast("serverRpcHandler", new RpcServerSocketHandler());
                }
            });

        try {
            this.port = getAvailablePort();
            this.channelFuture = serverBootstrap.bind(this.port).sync();
            this.channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static final int MIN_PORT = 49152;
    public static final int MAX_PORT = 65535;

    /**
     * 获取区间内空闲端口
     */
    public int getAvailablePort() {
        for (int i = MIN_PORT; i <= MAX_PORT; i++) {
            try (ServerSocket serverSocket = new ServerSocket(i, 1)) {
                logger.debug("ServerSocket start with port: " + serverSocket.getLocalPort());
                return serverSocket.getLocalPort();
            } catch (IOException ignored) {
            }
        }
        throw new RuntimeException("Unable to find a free port in range [" + MIN_PORT + ", " + MAX_PORT + "]");
    }

    public String getServiceUrl() {
        if (channelFuture != null) {
            InetSocketAddress localAddress = (InetSocketAddress) channelFuture.channel().localAddress();
            String ip = localAddress.getHostString();
            return ip + ":" + this.port;
        }
        return null;
    }
}

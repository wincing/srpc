package org.jundeng.srpc.core.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import java.net.ServerSocket;
import org.jundeng.srpc.core.network.message.Request;

/**
 * 服务端：发送Response，接收Request
 */
public class RpcServerSocket implements Runnable {
    private ChannelFuture channelFuture;
    private final Request request;

    public RpcServerSocket(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {

            }
        });

        try {
            this.channelFuture = serverBootstrap.bind(getAvailablePort()).sync();
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
                return serverSocket.getLocalPort();
            } catch (IOException ignored) {
            }
        }
        throw new RuntimeException("Unable to find a free port in range [" + MIN_PORT + ", " + MAX_PORT + "]");
    }
}

package org.jundeng.srpc.core.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jundeng.srpc.core.network.message.Request;

/**
 * 客户端：发送Request，接收Response
 */
public class RpcClientSocket implements Runnable {

    private ChannelFuture future;

    private final String host;

    private Integer port;

    private final Request request;

    public RpcClientSocket(Request request) {
        this.request = request;
        this.host = request.getHost();
        this.port = request.getPort();
    }

    @Override
    public void run() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                }
            });

        try {
            this.future = bootstrap.connect(host, port).sync();
            this.future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }
}

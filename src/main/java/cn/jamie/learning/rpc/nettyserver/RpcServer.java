package cn.jamie.learning.rpc.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author xujing
 * @see
 */
public class RpcServer {

  private int port;

  public RpcServer(final int port) {
    this.port = port;
  }

  public void start() {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(bossGroup, workGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .localAddress(port)
        .childHandler(new ChannelInitializer<SocketChannel>() {

          protected void initChannel(final SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline
                .addLast("decoder",
                    new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                .addLast("encoder", new ObjectEncoder())
                .addLast(new RequestHandler());
          }
        });
    ChannelFuture future = null;
    try {
      future = serverBootstrap.bind(port).sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    future.channel().closeFuture();
  }

  public static void main(String[] args) {
    new RpcServer(9999).start();
  }
}

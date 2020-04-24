package cn.jamie.learning.rpc.nettyclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.jamie.learning.rpc.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 远程服务代理，用于创建netty客户端
 * 发起连接
 * 序列化请求数据
 * 并接受返回结果进行反序列解析
 * <p>
 * 编码器解码器client和server要相同
 *
 * @author xujing
 * @see
 */
public class RemoteServiceProxy {

  public static Object create(final String host, final int port, final Class target) {

    return Proxy.newProxyInstance(target.getClassLoader(), new Class[] { target },
        new InvocationHandler() {

          public Object invoke(final Object proxy, final Method method, final Object[] args)
              throws Throwable {
            // server端实现服务注册前配合，在server端配合getImplClassName使用
            // target.getName() :class 的全路径
            //  ClassInfo classInfo =
            //  new ClassInfo(target.getName(), method.getName(), method.getParameterTypes(), args);

            //服务注册后，只需要class的type name就可找到服务
            String className = target.getTypeName();
            RpcRequest rpcRequest =
                new RpcRequest(className, method.getName(), method.getParameterTypes(), args);
            EventLoopGroup group = new NioEventLoopGroup();
            final ResponseHandler responseHandler = new ResponseHandler();
            try {

              Bootstrap bootstrap = new Bootstrap();
              bootstrap.group(group)
                  .channel(NioSocketChannel.class)
                  .handler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(final SocketChannel socketChannel) throws Exception {
                      ChannelPipeline pipeline = socketChannel.pipeline();
                      //编码器
                      pipeline.addLast("encoder", new ObjectEncoder())
                          //解码器,构造方法第一个参数设置二进制的最大字节数,第二个参数设置具体使用哪个类解析器
                          .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                              ClassResolvers.cacheDisabled(null)))
                          //客户端业务处理类
                          .addLast("handler", responseHandler);
                    }
                  });
              ChannelFuture future = bootstrap.connect(host, port).sync();
              future.channel().writeAndFlush(rpcRequest).sync();
              future.channel().closeFuture().sync();
            } finally {
              group.shutdownGracefully();
            }
            return responseHandler.getResponse();
          }
        });
  }

}

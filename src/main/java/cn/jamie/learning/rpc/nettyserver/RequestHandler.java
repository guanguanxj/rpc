package cn.jamie.learning.rpc.nettyserver;

import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;

import cn.jamie.learning.rpc.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author xujing
 */
public class RequestHandler extends ChannelInboundHandlerAdapter {

  /**
   * 服务查找
   * replace by ServiceRepository.Java
   */
  @Deprecated
  private String getImplClassName(RpcRequest rpcRequest) throws ClassNotFoundException {
    int lastDot = rpcRequest.getClassName().lastIndexOf(".");
    String interfaceName = rpcRequest.getClassName().substring(lastDot);
    String interfacePath = "cn.jamie.learning.rpc.nettyserver.service";
    Class<?> superClazz = Class.forName(interfacePath + interfaceName);
    Reflections reflections = new Reflections(interfacePath);
    Set<Class<?>> impClazzSet = (Set<Class<?>>) reflections.getSubTypesOf(superClazz);
    if (impClazzSet.size() == 0) {
      System.out.println("can't find the impl class");
    } else if (impClazzSet.size() > 1) {
      System.out.println("not assign the impl one ");
    } else {
      Class[] array = impClazzSet.toArray(new Class[0]);
      return array[0].getName();
    }
    return null;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    RpcRequest rpcRequest = (RpcRequest) msg;
    //    Class.forName(getImplClassName(classInfo));
    Object clazzInstance = ServiceRepository.getInstance(rpcRequest.getClassName());
    Method method =
        clazzInstance.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
    Object result = method.invoke(clazzInstance, rpcRequest.getParamObjects());
    ctx.writeAndFlush(result);
  }
}

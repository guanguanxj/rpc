package cn.jamie.learning.rpc.nettyclient;

import cn.jamie.learning.rpc.nettyserver.service.HelloWorld;

/**
 * 模拟客户端调用远程服务127.0.0.1:9999下的HelloWorld
 *
 * @author xujing
 */
public class RpcClient {

  public static void main(String[] args) {
    // todo host port改成可配置化
    HelloWorld helloWorld =
        (HelloWorld) RemoteServiceProxy.create("127.0.0.1", 9999, HelloWorld.class);
    System.out.println(helloWorld.helloWorld("jamie"));
  }
}

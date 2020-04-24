package cn.jamie.learning.rpc.nettyserver.service;

import cn.jamie.learning.rpc.RpcService;

/**
 * @author xujing
 * @see
 */

@RpcService(HelloWorld.class)
public class HelloWorldImpl implements HelloWorld {

  public String helloWorld(String name) {
    return "hello " + name + ", my first rpc worked";
  }
}

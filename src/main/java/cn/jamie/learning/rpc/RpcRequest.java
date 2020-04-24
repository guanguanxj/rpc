package cn.jamie.learning.rpc;

import java.io.Serializable;

/**
 * rpc远程调用的请求封装
 *
 * @author xujing
 * @see
 */
public class RpcRequest implements Serializable {

  private String className;
  private String methodName;
  private Class<?>[] paramTypes;
  private Object[] paramObjects;

  public RpcRequest() {
  }

  public RpcRequest(final String className, final String methodName, final Class<?>[] paramTypes,
      final Object[] paramObjects) {
    this.className = className;
    this.methodName = methodName;
    this.paramTypes = paramTypes;
    this.paramObjects = paramObjects;
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public Class<?>[] getParamTypes() {
    return paramTypes;
  }

  public Object[] getParamObjects() {
    return paramObjects;
  }
}

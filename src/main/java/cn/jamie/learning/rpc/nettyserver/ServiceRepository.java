package cn.jamie.learning.rpc.nettyserver;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.jamie.learning.rpc.RpcService;

/**
 * todo 分布式场景可使用zookeeper来实现服务注册中心
 * 服务注册中心的简单实现
 *
 * @author xujing
 */
public class ServiceRepository {

  private static final Map<String, Class<?>> SERVICES = new ConcurrentHashMap();

  static {
    try {
      registerClass("cn.jamie.learning.rpc.nettyserver.service");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static void registerClass(String packageName) throws ClassNotFoundException {
    // 需要查找的结果
    // 找到指定的包目录
    File directory = null;
    try {
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      if (cld == null)
        throw new ClassNotFoundException("can not get the ClassLoader");
      String path = packageName.replace('.', '/');
      URL resource = cld.getResource(path);
      if (resource == null)
        throw new ClassNotFoundException(path + "'s resource not found ");
      directory = new File(resource.getFile());
    } catch (NullPointerException x) {
      throw new ClassNotFoundException(packageName + " (" + directory + ") invalid");
    }
    if (directory.exists()) {
      // 获取包目录下的所有文件
      String[] files = directory.list();
      File[] fileList = directory.listFiles();
      // 获取包目录下的所有文件
      for (int i = 0; fileList != null && i < fileList.length; i++) {
        File file = fileList[i];
        //判断是否是Class文件
        if (file.isFile() && file.getName().endsWith(".class")) {
          Class<?> clazz =
              Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6));
          if (clazz.getAnnotation(RpcService.class) != null) {
            SERVICES.put(clazz.getAnnotation(RpcService.class).value().getName(), clazz);
          }
        } else if (file.isDirectory()) { //如果是目录，递归查找
          registerClass(packageName + "." + file.getName());
        }
      }
    } else {
      throw new ClassNotFoundException(packageName + "is invalid");
    }
  }

  // 提供服务实例
  public static Object getInstance(String className) {
    Class<?> clazz = SERVICES.get(className);
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

}

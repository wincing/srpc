package org.jundeng.srpc.core.reflect.invoke;

import java.lang.reflect.Proxy;
import org.jundeng.srpc.common.util.ClassLoaderUtil;

/**
 * 代理类获取
 */
public class RpcProxy {

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Class<T> interfaceClass) {
        RpcInvocationHandler handler = new RpcInvocationHandler(interfaceClass.getName());
        ClassLoader classLoader = ClassLoaderUtil.getCurrentClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[] {interfaceClass}, handler);
    }
}

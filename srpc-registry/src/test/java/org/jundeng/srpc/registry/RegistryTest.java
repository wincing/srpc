package org.jundeng.srpc.registry;

import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RegistryTest {

    @Test
    public void testRedisGetAndRegister() {
        Registry redisRegistry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension("redis");


        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setUrl("111.0.0.1:9999");
        serviceInfo.setInterfaceName("org.jundeng.srpc.registry.redis.RedisRegistry#doRegister");
        redisRegistry.register(serviceInfo);

        final String service = redisRegistry.getService(serviceInfo);
        System.out.println(service);
    }


    @Test
    public void testJedisConnection() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(10); // 最大空闲连接
        config.setTestOnBorrow(false);

        // 获取连接超时时间5s
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 6379, 5 * 1000);
        try (final Jedis resource = jedisPool.getResource()) {

        }
    }
}

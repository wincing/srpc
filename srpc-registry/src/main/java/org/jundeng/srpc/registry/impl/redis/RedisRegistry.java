package org.jundeng.srpc.registry.impl.redis;

import static org.jundeng.srpc.common.config.ConfigManager.getConfigManager;

import cn.hutool.core.thread.NamedThreadFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jundeng.srpc.common.Constants;
import org.jundeng.srpc.common.config.ConfigManager;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.jundeng.srpc.common.util.StringUtil;
import org.jundeng.srpc.loadbalance.LoadBalance;
import org.jundeng.srpc.loadbalance.common.ServiceInfo;
import org.jundeng.srpc.registry.AbstractRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

// todo: 引入多个连接池支持redis集群
// todo: 失败恢复
// todo: bug 当服务治理中心下线时，调用方和接收方仍可以设置消息

/**
 * Redis注册中心
 *
 * 存储数据结构
 * Redis.key: srpc/com.jundeng.xxxService/providers_alias
 * Redis.value: Map {Map.key=URL, Map.value=expireTime}
 *
 */
public class RedisRegistry extends AbstractRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RedisRegistry.class);

    /** 超时时间间隔 单位:ms **/
    private Long expirePeriod;

    private JedisPool jedisPool;

    private LoadBalance loadBalance;


    /** 续费服务守护线程 **/
    private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("SrpcRegistryExpireTimer", true));

    private final ScheduledFuture<?> expireFuture;

    /** 订阅的服务 {key:服务名， value: 服务提供方列表}**/
    private final Map<String, ArrayList<String>> subscribed = new ConcurrentHashMap<>();

    /** 事件监听线程 {key:服务名， value: 对应服务pub事件监听器} **/
    private final ConcurrentMap<String, Notifier> notifiers = new ConcurrentHashMap<String, Notifier>();

    /** 标记是否是服务治理中心 **/
    private boolean admin = false;

    public RedisRegistry() {
        initRedis();

        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    deferExpired();
                } catch (Throwable t) {
                    logger.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
                }
            }
        }, expirePeriod / 2, expirePeriod / 2, TimeUnit.MILLISECONDS); // 设置为超时时间的一半
    }

    /**
     * 初始化redis连接
     */
    private void initRedis() {
        ConfigManager configManager = getConfigManager();
        RedisConfig redisConfig = configManager.loadConfig(RedisConfig.class);

        this.expirePeriod = redisConfig.getServiceTimeout().longValue();
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(redisConfig.getLoadBalance());

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(10); // 最大空闲连接
        config.setTestOnBorrow(false);

        // 获取连接超时时间5s
        if (StringUtil.isBlank(redisConfig.getPassword())) {
            this.jedisPool = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), 5 * 1000);
        }else {
            this.jedisPool = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), 5 * 1000, redisConfig.getPassword());
        }
    }

    /**
     * format: srpc/com.jundeng.xxxService/providers_alias
     */
    private String toCategoryPath(ServiceInfo serviceInfo) {
        return Constants.SRPC_SERVICE_PREFIX + serviceInfo.getInterfaceName() + Constants.URL_SEPARATOR + serviceInfo.getAlias();
    }

    /**
     * 服务续期
     */
    private void deferExpired() {
        try (Jedis jedis = jedisPool.getResource()) {
            for (ServiceInfo serviceInfo : getRegistered()) {
                String key = toCategoryPath(serviceInfo);
                if (jedis.hset(key, serviceInfo.getUrl(), String.valueOf(System.currentTimeMillis() + expirePeriod)) == 1) {
                    jedis.publish(key, Constants.REGISTER);
                }
            }
            // 如果是服务治理中心，则还要清理过期的key
            if (admin) {
                clean(jedis);
            }
        }
    }

    /**
     * 清除过期服务，由服务中心调用
     */
    private void clean(Jedis jedis) {
        // 获取所有服务
        Set<String> keys = jedis.keys(Constants.SRPC_SERVICE_PREFIX + Constants.ANY_VALUE);
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // 获取服务的所有提供方
                final Map<String, String> values = jedis.hgetAll(key);
                boolean delete = false;
                if (values != null && !values.isEmpty()) {
                    long now = System.currentTimeMillis();
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        long expireTime = Long.parseLong(entry.getValue());
                        if (expireTime < now) {
                            jedis.hdel(key, entry.getKey());
                            delete = true;
                            logger.warn("Delete expired service provider: " + key + "->url: " + entry.getKey() + ", expire: " + new Date(expireTime) + ", now: " + new Date(now));
                        }
                    }
                }
                if (delete) {
                    jedis.publish(key, Constants.UNREGISTER);
                }
            }
        }
    }

    @Override
    public Boolean register(ServiceInfo serviceInfo) {
        String key = toCategoryPath(serviceInfo);
        String url = serviceInfo.getUrl();
        boolean success = false;
        try (Jedis jedis = jedisPool.getResource()) {
            success = jedis.hset(key, url, String.valueOf(System.currentTimeMillis() +  expirePeriod)) > 0;
            jedis.publish(key, Constants.REGISTER);
        } catch (Throwable t) {
           logger.error("Failed to register service to redis registry, service: " + url + ", cause: " + t.getMessage());
        }
        return success;
    }

    @Override
    public String getService(ServiceInfo serviceInfo) {
        String serviceKey = toCategoryPath(serviceInfo);
        Notifier notifier = notifiers.get(serviceKey);
        // 第一次订阅
        if (notifier == null) {
            updateService(serviceKey);
            // 订阅通道事件
            notifier = new Notifier(serviceInfo);
            notifiers.put(serviceKey, notifier);
            notifier.start();
        }

        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        for (String url : subscribed.get(serviceKey)) {
            final ServiceInfo s = new ServiceInfo();
            s.setUrl(url);
            s.setInterfaceName(serviceInfo.getInterfaceName());
            serviceInfoList.add(s);
        }
        return loadBalance.select(serviceInfoList);
    }

    @Override
    public void unRegister(ServiceInfo serviceInfo) {
    }

    /**
     * 程序退出时调用
     * todo: 退出清空内存数据 Hook
     */
    public void destroy() {
        try {
            expireFuture.cancel(true);
            for (Notifier notifier : notifiers.values()) {
                notifier.shutdown();
            }
            if (admin) {
                try (Jedis jedis = jedisPool.getResource()) {

                }
            }
            jedisPool.close();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    /** 更新服务 **/
    private void updateService(String serviceKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            ArrayList<String> providers = subscribed.get(serviceKey);
            if (providers == null) {
                subscribed.put(serviceKey, new ArrayList<>());
                providers = subscribed.get(serviceKey);
            }
            providers.clear();
            providers.addAll(jedis.hgetAll(serviceKey).keySet());
        }
    }

    private class NotifySub extends JedisPubSub {
        @Override
        public void onMessage(String key, String msg) {
            if (msg.equals(Constants.REGISTER)  || msg.equals(Constants.UNREGISTER)) {
                updateService(key);
            }
        }

        @Override
        public void onPMessage(String pattern, String key, String msg) {
            onMessage(key, msg);
        }

        @Override
        public void onSubscribe(String key, int num) {
        }

        @Override
        public void onPSubscribe(String pattern, int num) {
        }

        @Override
        public void onUnsubscribe(String key, int num) {
        }

        @Override
        public void onPUnsubscribe(String pattern, int num) {
        }
    }

    private class Notifier extends Thread {
        private final ServiceInfo serviceInfo;
        private volatile boolean running = true;
        private volatile boolean first = true;

        public Notifier(ServiceInfo serviceInfo) {
            this.serviceInfo = serviceInfo;
        }

        @Override
        public void run() {
            while (running) {
                try (Jedis jedis = jedisPool.getResource()) {
                    if (first) {
                        String serviceKey = toCategoryPath(serviceInfo);
                        jedis.subscribe(new NotifySub(), serviceKey);
                        first = false;
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }

}

package com.syfen.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.curator.framework.CuratorFramework;
import com.syfen.redis.exceptions.RedisAlreadyInitializedException;
import com.syfen.redis.exceptions.RedisNotInitializedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * User: ToneD
 * Created: 16/09/13 10:07 AM
 */
public class Redis {

    private static final Logger log = LoggerFactory.getLogger(Redis.class);
    public static final Cache<String, RedisClusterCacheItem> cache = CacheBuilder.newBuilder().concurrencyLevel(64)
            .build();
    private static boolean initialized = false;
    private static RedisClusterConfig config;
    private static CuratorFramework zkClient;

    public synchronized static void initialize(CuratorFramework zookeeperClient, RedisClusterConfig clusterConfig)
            throws RedisAlreadyInitializedException {

        if(!initialized) {
            zkClient = zookeeperClient;
            config = clusterConfig;
            initialized = true;
        }
        else {
            throw new RedisAlreadyInitializedException();
        }
    }

    public static RedisCluster getCluster(String clusterName) throws RedisNotInitializedException {

        if(initialized) {
            RedisClusterCacheItem cachedItem = cache.getIfPresent(clusterName);

            if (cachedItem != null) {
                return cachedItem.getCluster();
            }

            return initializeCluster(clusterName);
        }
        else {
            throw new RedisNotInitializedException("Redis must be initialized prior to getting a cluster.");
        }
    }

    public static ArrayList<String> getClusterList() {

        Set<String> keys = cache.asMap().keySet();
        ArrayList list = new ArrayList();

        for (String key : keys) {
            list.add(key.toString());
        }

        return list;
    }

    public static Map<String, RedisClusterCacheItem> getClusters() {

        return cache.asMap();
    }

    public static int getClusterCount() {

        return cache.asMap().size();
    }

    private synchronized static RedisCluster initializeCluster(String clusterName) {

        RedisClusterCacheItem cachedItem = cache.getIfPresent(clusterName);

        if (cachedItem != null) {
            return cachedItem.getCluster();
        }

        RedisCluster cluster = new RedisCluster(clusterName, config, zkClient);

        cache.put(clusterName, new RedisClusterCacheItem(clusterName, cluster));

        return cluster;
    }
}

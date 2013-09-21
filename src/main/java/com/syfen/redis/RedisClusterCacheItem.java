package com.syfen.redis;

/**
 * User: ToneD
 * Created: 16/09/13 10:45 AM
 */
public class RedisClusterCacheItem {

    private String name;
    private RedisCluster cluster;

    public RedisClusterCacheItem(String clusterName, RedisCluster redisCluster) {

        this.name = clusterName;
        this.cluster = redisCluster;
    }

    public String getName() {
        return name;
    }

    public RedisCluster getCluster() {
        return cluster;
    }
}

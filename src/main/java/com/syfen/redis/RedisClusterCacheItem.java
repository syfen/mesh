package com.syfen.redis;

/**
 * User: ToneD
 * Created: 16/09/13 10:45 AM
 */
public class RedisClusterCacheItem {

    public String clusterName;
    public RedisCluster redisCluster;

    public RedisClusterCacheItem(String clusterName, RedisCluster redisCluster) {

        this.clusterName = clusterName;
        this.redisCluster = redisCluster;
    }
}

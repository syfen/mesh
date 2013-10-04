package com.syfen.redis;

import com.netflix.config.ConfigurationManager;
import com.netflix.curator.framework.CuratorFramework;
import com.syfen.redis.exceptions.RedisAlreadyInitializedException;
import com.syfen.redis.exceptions.RedisNotInitializedException;
import com.syfen.zookeeper.ExhibitorListProvider;
import com.syfen.zookeeper.ZookeeperClientFactory;
import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * User: ToneD
 * Created: 19/09/13 3:50 PM
 */
public class RedisTest {

    private static final Logger log = LoggerFactory.getLogger(RedisTest.class);
    private static final AbstractConfiguration config = ConfigurationManager.getConfigInstance();
    private static CuratorFramework zkClient;

    @BeforeClass
    public static void setUp() {

        // initialize zookeeper client factory params
        String zkConfigRootPath = "/" + ConfigurationManager.getDeploymentContext().getDeploymentEnvironment();

        // setup zookeeper dynamic configuration
        try {
            ZookeeperClientFactory.initializeAndStartZkConfigSource(ExhibitorListProvider.getExhibitorList(),
                    ConfigurationManager.getDeploymentContext().getApplicationId(), zkConfigRootPath);
            zkClient = ZookeeperClientFactory.getStartedZKClient(ExhibitorListProvider.getExhibitorList(),
                    ConfigurationManager.getDeploymentContext().getApplicationId());
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            Redis.initialize(zkClient, new RedisClusterConfig());
        }
        catch(Exception e) {}
    }

    @Test(expected = RedisAlreadyInitializedException.class)
    public void testAlreadyInitialized() throws RedisAlreadyInitializedException {

        Redis.initialize(zkClient, new RedisClusterConfig());
    }

    @Test
    public void testClusterCreation() {

        try {
            Redis.getCluster("session");
        }
        catch (RedisNotInitializedException e) {
            log.error(e.getMessage());
        }
    }
}

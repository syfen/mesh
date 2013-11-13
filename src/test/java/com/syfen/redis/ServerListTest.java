package com.syfen.redis;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.api.CuratorEvent;
import com.netflix.curator.framework.api.CuratorListener;
import com.syfen.zookeeper.ZookeeperClientFactory;
import com.syfen.zookeeper.exhibitor.ExhibitorS3ListProvider;
import junit.framework.Assert;
import org.apache.zookeeper.Watcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: ToneD
 * Created: 11/11/2013 3:25 PM
 */
public class ServerListTest {

    private static final Logger log = LoggerFactory.getLogger(ServerListTest.class);
    private static CuratorFramework zkClient;

    @BeforeClass
    public static void setUp() throws Exception {

        zkClient = ZookeeperClientFactory.getStartedZKClient(new ExhibitorS3ListProvider(), "redis-cluster");

        try {
            Redis.initialize(zkClient, new ClusterConfig().withRootNode("unit"));
        }
        catch(Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void manualServerAdd() throws Exception {

        final CountDownLatch lock = new CountDownLatch(3);
        final String clusterNodeName = "server-list-test-manual-add";
        final String testServerName = "test-add-redis-server";
        final String serversPath;

        Cluster cluster = Redis.getCluster(clusterNodeName);

        if(cluster.getConfig().getRootNode() == null) {
            serversPath = "/" + clusterNodeName + "/" + Constants.ZK_DEFAULT_REDIS_SERVERS_NODE;
        }
        else {
            serversPath = "/" + cluster.getConfig().getRootNode() + "/" + clusterNodeName + "/" +
                    Constants.ZK_DEFAULT_REDIS_SERVERS_NODE;
        }

        final String testServerPath = serversPath + "/" + testServerName;

        CuratorListener curatorListener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {

                if(event.getWatchedEvent().getPath().equals(serversPath)) {
                    if(event.getWatchedEvent().getType().equals(Watcher.Event.EventType.NodeChildrenChanged)) {
                        if(zkClient.checkExists().forPath(testServerPath) != null) {
                            lock.countDown();
                        }
                    }
                }
            }
        };

        ServerEventListener serverListEventListener = new ServerEventListener() {
            @Override
            public void eventReceived(ServerEventType type, Server server) {
                if(type.equals(ServerEventType.ServerAdded) && server.getName().equals(testServerName)) {
                    lock.countDown();
                }
            }
        };

        ServerEventListener clusterEventListener = new ServerEventListener() {
            @Override
            public void eventReceived(ServerEventType type, Server server) {
                if(type.equals(ServerEventType.ServerAdded) && server.getName().equals(testServerName)) {
                    lock.countDown();
                }
            }
        };

        // make sure server node doesn't already exist
        if(zkClient.checkExists().forPath(testServerPath) != null) {
            zkClient.delete().forPath(testServerPath);
        }

        // setup listeners
        zkClient.getCuratorListenable().addListener(curatorListener);
        zkClient.getChildren().watched().forPath(serversPath);
        cluster.getServerList().addListener(serverListEventListener);
        cluster.addListener(clusterEventListener);

        // add server
        zkClient.create().forPath(testServerPath);

        Assert.assertTrue(lock.await(10000, TimeUnit.MILLISECONDS));

        ServerList serverList = cluster.getServerList();

        // test server is in the server list
        Assert.assertTrue(serverList.contains(testServerName));

        // clean up - delete node
        zkClient.delete().forPath(testServerPath);
    }

    @Test
    public void manualServerRemove() throws Exception{

        final CountDownLatch lock = new CountDownLatch(3);
        final String clusterNodeName = "server-list-test-manual-remove";
        final String testServerName = "test-remove-redis-server";
        final String serversPath;

        Cluster cluster = Redis.getCluster(clusterNodeName);

        if(cluster.getConfig().getRootNode() == null) {
            serversPath = "/" + clusterNodeName + "/" + Constants.ZK_DEFAULT_REDIS_SERVERS_NODE;
        }
        else {
            serversPath = "/" + cluster.getConfig().getRootNode() + "/" + clusterNodeName + "/" +
                    Constants.ZK_DEFAULT_REDIS_SERVERS_NODE;
        }

        final String testServerPath = serversPath + "/" + testServerName;

        CuratorListener curatorListener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {

                if(event.getWatchedEvent().getPath().equals(serversPath)) {
                    if(event.getWatchedEvent().getType().equals(Watcher.Event.EventType.NodeChildrenChanged)) {
                        if(zkClient.checkExists().forPath(testServerPath) == null) {
                            lock.countDown();
                        }
                    }
                }
            }
        };

        ServerEventListener serverListEventListener = new ServerEventListener() {
            @Override
            public void eventReceived(ServerEventType type, Server server) {
                if(type.equals(ServerEventType.ServerRemoved) && server.getName().equals(testServerName)) {
                    lock.countDown();
                }
            }
        };

        ServerEventListener clusterEventListener = new ServerEventListener() {
            @Override
            public void eventReceived(ServerEventType type, Server server) {
                if(type.equals(ServerEventType.ServerRemoved) && server.getName().equals(testServerName)) {
                    lock.countDown();
                }
            }
        };

        // create server node if it does not exist
        if(zkClient.checkExists().forPath(testServerPath) == null) {
            zkClient.create().forPath(testServerPath);
        }

        // setup listeners
        zkClient.getCuratorListenable().addListener(curatorListener);
        zkClient.getChildren().watched().forPath(serversPath);
        cluster.getServerList().addListener(serverListEventListener);
        cluster.addListener(clusterEventListener);

        // remove server
        zkClient.delete().forPath(testServerPath);

        Assert.assertTrue(lock.await(10000, TimeUnit.MILLISECONDS));

        ServerList serverList = cluster.getServerList();

        // test server is in the server list
        Assert.assertFalse(serverList.contains(testServerName));
    }
}

package com.syfen.redis;

/**
 * User: ToneD
 * Created: 16/09/13 12:26 PM
 */
public class RedisClusterConfig {

    // set default values
    private String rootNode = Constants.ZK_DEFAULT_REDIS_ROOT_NODE;
    private String clientsNode = Constants.ZK_DEFAULT_REDIS_CLIENTS_NODE;
    private String serversNode = Constants.ZK_DEFAULT_REDIS_SERVERS_NODE;
    private String hostnameNode = Constants.ZK_DEFAULT_REDIS_HOSTNAME_NODE;
    private String portNode = Constants.ZK_DEFAULT_REDIS_PORT_NODE;

    public String getRootNode() {
        return rootNode;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode;
    }

    public RedisClusterConfig withRootNode(String rootNode) {
        this.rootNode = rootNode;
        return this;
    }

    public String getClientsNode() {
        return clientsNode;
    }

    public void setClientsNode(String clientsNode) {
        this.clientsNode = clientsNode;
    }

    public RedisClusterConfig withClientsNode(String clientsNode) {
        this.clientsNode = clientsNode;
        return  this;
    }

    public String getServersNode() {
        return serversNode;
    }

    public void setServersNode(String serversNode) {
        this.serversNode = serversNode;
    }

    public RedisClusterConfig withServersNode(String serversNode) {
        this.serversNode = serversNode;
        return this;
    }

    public String getHostnameNode() {
        return hostnameNode;
    }

    public void setHostnameNode(String hostnameNode) {
        this.hostnameNode = hostnameNode;
    }

    public RedisClusterConfig withHostnameNode(String hostnameNode) {
        this.hostnameNode = hostnameNode;
        return this;
    }

    public String getPortNode() {
        return portNode;
    }

    public void setPortNode(String portNode) {
        this.portNode = portNode;
    }

    public RedisClusterConfig withPortNode(String portNode) {
        this.portNode = portNode;
        return this;
    }
}

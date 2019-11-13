package com.sh.mlshjob.config;

import com.tuan.job.client.service.JobcenterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author
 * @version 2019/5/23.
 */
@Component
@ConfigurationProperties(prefix = "jobcenter")
public class JobConfig {
    private String name;
    private String port;
    private String serverHost;
    private String serverPort;
    private String zkAddress;

    @PostConstruct
    public void post() {
        JobcenterConfig.APP_NAME = name;
        JobcenterConfig.APP_PORT = port;
        JobcenterConfig.JOBCENTER_HOST = serverHost;
        JobcenterConfig.JOBCENTER_PORT = serverPort;
        JobcenterConfig.ZOOKEEPER_ADDRESS = zkAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }
}

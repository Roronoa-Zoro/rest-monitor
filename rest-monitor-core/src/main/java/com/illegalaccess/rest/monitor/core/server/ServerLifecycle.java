package com.illegalaccess.rest.monitor.core.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */
@Component
public class ServerLifecycle {

    @Autowired
    private List<MonitorServer> servers;

    @PostConstruct
    public void initServers() throws Exception {
        for (MonitorServer ms : servers) {
            ms.startServer();
        }
    }
}

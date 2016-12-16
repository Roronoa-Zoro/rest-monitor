package com.illegalaccess.rest.monitor.client.lb.eureka;

import com.illegalaccess.rest.monitor.client.lb.RegisterHost;
import com.illegalaccess.rest.monitor.client.lb.ServiceDetector;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2016/12/15.
 * 使用eureka做服务发现
 */
@Slf4j
@Service("eurekaServiceDetector")
public class EurekaServiceDetector implements ServiceDetector {

    private ApplicationInfoManager applicationInfoManager;
    private EurekaClient eurekaClient;
    @Value("${vipAddress}")
    private String vipAddress;

    private synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }

        return applicationInfoManager;
    }

    private synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        if (eurekaClient == null) {
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }

        return eurekaClient;
    }

    @PostConstruct
    public void initDetector() {
        initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
        log.info("eureka detector is running...");
    }

    public RegisterHost discoverRemoteHost() {
        InstanceInfo nextServerInfo;
        try {
            nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
        } catch (Exception e) {
            log.error("Cannot get an instance of example service to talk to from eureka", e);
            throw new RuntimeException(e);
        }
        RegisterHost rh = new RegisterHost();
        rh.setRemoteHostIP(nextServerInfo.getIPAddr());
        rh.setRemoteHostPort(nextServerInfo.getPort());
        return rh;
    }
}

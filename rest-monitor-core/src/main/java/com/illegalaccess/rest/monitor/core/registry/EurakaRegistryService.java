package com.illegalaccess.rest.monitor.core.registry;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/19.
 */
@Slf4j
@Service
public class EurakaRegistryService implements RegistryService {

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

    private void registerSelf() {
        log.info("Registering service to eureka with STARTING status");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);

        log.info("Simulating service initialization by sleeping for 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Nothing
        }

        // Now we change our status to UP
        System.out.println("Done sleeping, now changing status to UP");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka(eurekaClient);
    }

    private void waitForRegistrationWithEureka(EurekaClient eurekaClient) {
        // my vip address to listen on
        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        String localVIPAddress = configInstance.getStringProperty(vipAddress, "sampleservice.mydomain.net").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka(localVIPAddress, false);
            } catch (Throwable e) {
                log.info("Waiting ... verifying service registration with eureka ...", e);
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void registerService() {
        initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
        registerSelf();
    }
}

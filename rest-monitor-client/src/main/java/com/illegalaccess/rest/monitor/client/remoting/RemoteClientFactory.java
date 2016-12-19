package com.illegalaccess.rest.monitor.client.remoting;

import com.illegalaccess.rest.monitor.client.lb.ServiceDetector;
import com.illegalaccess.rest.monitor.client.remoting.tcp.TCPRemoteClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Created by Administrator on 2016/12/19.
 */
@Component
public class RemoteClientFactory implements FactoryBean<RemoteClient> {

    @Value("${monitor.client.protocol}")
    private String protocol;
    private String UDT_PROTOCOL = "UDT";
    private String TCP_PROTOCOL = "TCP";
    @Autowired
    private ServiceDetector serviceDetector;

    @Override
    public RemoteClient getObject() throws Exception {
        if (serviceDetector == null) {
            throw new IllegalArgumentException("ServiceDetector must be set");
        }
        if (StringUtils.isBlank(protocol)) {
            throw new IllegalArgumentException("ServiceDetector must be set");
        }

        if (UDT_PROTOCOL.equalsIgnoreCase(protocol)) {
            return null;
        }
        if (TCP_PROTOCOL.equalsIgnoreCase(protocol)) {
            TCPRemoteClient tcpClient = new TCPRemoteClient(serviceDetector);
            tcpClient.startClient(serviceDetector.discoverRemoteHost());
            return tcpClient;
        }
        throw new IllegalArgumentException("protocol should be either tcp or udt");
    }

    @Override
    public Class<?> getObjectType() {
        return RemoteClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

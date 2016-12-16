package com.illegalaccess.rest.monitor.client.reporter;

import com.illegalaccess.rest.monitor.client.lb.ServiceDetector;
import com.illegalaccess.rest.monitor.client.reporter.tcp.TCPStatReporter;
import com.illegalaccess.rest.monitor.client.reporter.udp.UDPStatReporter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 2016/12/13.
 */
public class StatReporterFactory implements FactoryBean<StatReporter> {

    private String protocol;
    private String UDT_PROTOCOL = "UDT";
    private String TCP_PROTOCOL = "TCP";
    protected ServiceDetector serviceDetector;

    public void setServiceDetector(ServiceDetector serviceDetector) {
        this.serviceDetector = serviceDetector;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public StatReporter getObject() throws Exception {
        //TODO 根据protocol判断创建udp 还是 tcp的实现
        if (serviceDetector == null) {
            throw new IllegalArgumentException("ServiceDetector must be set");
        }
        if (StringUtils.isBlank(protocol)) {
            StatReporter reporter = new UDPStatReporter();
            reporter.setServiceDetector(serviceDetector);
            return reporter;
        }

        if (UDT_PROTOCOL.equalsIgnoreCase(protocol)) {
            StatReporter reporter = new UDPStatReporter();
            reporter.setServiceDetector(serviceDetector);
            return reporter;
        }
        if (TCP_PROTOCOL.equalsIgnoreCase(protocol)) {
            StatReporter reporter = new TCPStatReporter();
            reporter.setServiceDetector(serviceDetector);
            return reporter;
        }
        throw new IllegalArgumentException("protocol should be either tcp or udt");
    }

    public Class<?> getObjectType() {
        return StatReporter.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

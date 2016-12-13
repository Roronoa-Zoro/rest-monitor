package com.illegalaccess.rest.monitor.client.reporter;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 2016/12/13.
 */
public class StatReporterFactory implements FactoryBean<StatReporter> {

    private String protocol = "UDP";

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public StatReporter getObject() throws Exception {
        //TODO 根据protocol判断创建udp 还是 tcp的实现
        return null;
    }

    public Class<?> getObjectType() {
        return StatReporter.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

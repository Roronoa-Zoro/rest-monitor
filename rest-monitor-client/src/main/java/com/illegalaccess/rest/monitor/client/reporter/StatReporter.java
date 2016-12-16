package com.illegalaccess.rest.monitor.client.reporter;

import com.illegalaccess.rest.monitor.client.lb.ServiceDetector;
import com.illegalaccess.rest.monitor.client.remoting.RemoteClient;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 * 数据上报接口
 */

public abstract class StatReporter {

    protected RemoteClient remoteClient;
    protected ServiceDetector serviceDetector;

    public void setServiceDetector(ServiceDetector serviceDetector) {
        this.serviceDetector = serviceDetector;
    }

    public void setRemoteClient(RemoteClient remoteClient) {
        this.remoteClient = remoteClient;
    }

    protected abstract void startReporter();

    public void submitReportData(List<InvocationStatVO> statData) {
        remoteClient.sendData(statData);
    }
}

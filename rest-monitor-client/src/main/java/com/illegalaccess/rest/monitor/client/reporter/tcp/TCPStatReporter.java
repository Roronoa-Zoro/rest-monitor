package com.illegalaccess.rest.monitor.client.reporter.tcp;

import com.illegalaccess.rest.monitor.client.remoting.tcp.TCPRemoteClient;
import com.illegalaccess.rest.monitor.client.reporter.StatReporter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2016/12/13.
 * 使用TCP协议上报数据,速度慢,可靠性高
 */
@Slf4j
public class TCPStatReporter extends StatReporter {

    public void startReporter() {
        TCPRemoteClient client = new TCPRemoteClient(serviceDetector);
        client.startClient(serviceDetector.discoverRemoteHost());
    }
}

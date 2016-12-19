package com.illegalaccess.rest.monitor.client.reporter;

import com.illegalaccess.rest.monitor.client.remoting.RemoteClient;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 * 数据上报接口
 */
@Component
public class StatReporter {

    @Autowired
    private RemoteClient remoteClient;

    public void submitReportData(List<InvocationStatVO> statData) {
        remoteClient.sendData(statData);
    }
}

package com.illegalaccess.rest.monitor.client.reporter;

import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 * 使用TCP协议上报数据,速度慢,可靠性高
 */
public class TCPStatReporter implements StatReporter {

    public void startReporter() {

    }

    public void submitReportData(List<InvocationStatVO> statData) {

    }
}

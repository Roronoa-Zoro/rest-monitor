package com.illegalaccess.rest.monitor.client.reporter;

import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 * 数据上报接口
 */
public interface StatReporter {

    void startReporter();

    void submitReportData(List<InvocationStatVO> statData);
}

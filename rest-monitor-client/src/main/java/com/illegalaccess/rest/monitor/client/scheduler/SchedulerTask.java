package com.illegalaccess.rest.monitor.client.scheduler;

import com.illegalaccess.rest.monitor.client.support.StatContainer;
import com.illegalaccess.rest.monitor.client.reporter.StatReporter;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/20.
 * send local stat data to server periodically
 */
@Slf4j
public class SchedulerTask {

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    //1 minute
    private final int defaultThreadhold = 1;

    private int periodBySecond;
    private int periodByMinute;
    private StatReporter reporter;

    public void setPeriodBySecond(int periodBySecond) {
        this.periodBySecond = periodBySecond;
    }

    public void setPeriodByMinute(int periodByMinute) {
        this.periodByMinute = periodByMinute;
    }

    public void setReporter(StatReporter reporter) {
        this.reporter = reporter;
    }

    public void initTask() {
        if (periodBySecond > 0) {
            executor.scheduleAtFixedRate(() -> {
                sendData();
            }, periodBySecond, periodBySecond, TimeUnit.SECONDS);
            return;
        }
        if (periodByMinute > 0) {
            executor.scheduleAtFixedRate(() -> {
                sendData();
            }, periodBySecond, periodByMinute, TimeUnit.MINUTES);
            return;
        }
        executor.scheduleAtFixedRate(() -> {
            sendData();
        }, defaultThreadhold, defaultThreadhold, TimeUnit.MINUTES);
    }

    private void sendData() {
        log.debug("begin to report stat data...");
        Collection<InvocationStatVO> data = StatContainer.Instance.getCurrentData();
        StatContainer.Instance.clear();
        LocalDateTime now = LocalDateTime.now();
        data.stream().forEach(stat -> stat.populateMaxAndMinCostAndReportTime(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        reporter.submitReportData(new ArrayList<>(data));
    }
}

package com.illegalaccess.rest.monitor.core.task;

import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import com.illegalaccess.rest.monitor.core.adapter.InfluxDBAdapter;
import com.illegalaccess.rest.monitor.core.queue.StatQueue;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jimmy Li on 2016/12/28.
 */
@Slf4j
public class StoreDataTask implements Runnable {

    private volatile boolean runFlag = true;

    private StatQueue statQueue;
    private int limit;
    private long interval;
    private TimeUnit timeUnit;
    private InfluxDBAdapter adapter;

    private LocalDateTime nextWriteTime;
    private ChronoUnit chronoUnit;
    private List<InvocationStatListProto.InvocationStatVO> tmpList = new ArrayList<>();

    public StoreDataTask(StatQueue statQueue, int limit, long interval, TimeUnit timeUnit, InfluxDBAdapter adapter) {
        this.statQueue = statQueue;
        this.limit = limit;
        this.interval = interval;
        this.timeUnit = timeUnit;
        if (TimeUnit.SECONDS == timeUnit) {
            this.chronoUnit = ChronoUnit.SECONDS;
        } else if (TimeUnit.MILLISECONDS == timeUnit) {
            this.chronoUnit = ChronoUnit.MILLIS;
        } else {
            throw new IllegalArgumentException("not supprted TimeUnit");
        }
        this.nextWriteTime = LocalDateTime.now().plus(interval, chronoUnit);
        this.adapter = adapter;
    }

    @Override
    public void run() {
        try {
            while (Thread.currentThread().isInterrupted()) {
                InvocationStatListProto.InvocationStatListVO result = statQueue.pollMessage(interval, timeUnit);
                if (result != null) {
                    tmpList.addAll(result.getStatVOListList());
                }

                if (LocalDateTime.now().isAfter(nextWriteTime)) {
                    if (!tmpList.isEmpty()) {
                        adapter.batchWrite(tmpList);
                        tmpList.clear();
                    }
                    this.nextWriteTime = LocalDateTime.now().plus(interval, chronoUnit);
                    continue;
                }

                if (tmpList.size() > limit) {
                    adapter.batchWrite(tmpList);
                    tmpList.clear();
                    this.nextWriteTime = LocalDateTime.now().plus(interval, chronoUnit);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.illegalaccess.rest.monitor.core.task;

import com.illegalaccess.rest.monitor.client.support.StatThreadFactory;
import com.illegalaccess.rest.monitor.core.adapter.InfluxDBAdapter;
import com.illegalaccess.rest.monitor.core.queue.StatQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jimmy Li on 2016/12/28.
 */
@Component
public class StoreTaskExecutor {

    @Autowired
    private StatQueue statQueue;
    @Value("${submit.limit}")
    private int limit;
    @Value("${submit.interval}")
    private long interval;
    @Value("${submit.interval.timeUnit}")
    private String timeUnit;
    @Autowired
    private InfluxDBAdapter adapter;

    @PostConstruct
    public void runTask() {
        TimeUnit tu;
        if (TimeUnit.SECONDS.name().equals(timeUnit)) {
            tu = TimeUnit.SECONDS;
        } else if (TimeUnit.MILLISECONDS.name().equals(timeUnit)) {
            tu = TimeUnit.MILLISECONDS;
        } else {
            throw new IllegalArgumentException("time unit should be second/millisecond");
        }
        ExecutorService es = Executors.newFixedThreadPool(20, new StatThreadFactory("StoreData-Thread"));
        for (int i = 0; i < 20; i++) {
            es.submit(new StoreDataTask(statQueue, limit, interval, tu, adapter));
        }
    }
}

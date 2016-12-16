package com.illegalaccess.rest.monitor.client.support;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/12/16.
 */
public class StatThreadFactory implements ThreadFactory {

    private AtomicInteger threadCount = new AtomicInteger(0);
    private String threadPrefix;
    private String defaultThreadPrefix = "StatThread-";

    public StatThreadFactory() {
    }

    public StatThreadFactory(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }

    public Thread newThread(Runnable r) {
        if (StringUtils.isBlank(threadPrefix)) {
            Thread t = new Thread(r, defaultThreadPrefix + threadCount.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
        Thread t = new Thread(r, threadPrefix + "-" + threadCount.getAndIncrement());
        t.setDaemon(true);
        return t;
    }
}

package com.illegalaccess.rest.monitor.client.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2016/12/13.
 */
@Data
@EqualsAndHashCode
public class InvocationStatVO {
    //总调用次数
    private AtomicLong invokeTimes = new AtomicLong(0);
    //调用失败次数
    private AtomicLong invokeFailureTimes = new AtomicLong(0);
    //总耗时
    private AtomicLong invokeTotalCost = new AtomicLong(0);
    //最低耗时
    private long invokeMinCost;
    //最大耗时
    private long invokeMaxCost;
    //提交数据时的时间戳
    private long reportTimestamp;

    //方法名
    private String methodName;
    //上报数据的host ip
    private String host;
    //应用系统名称
    private String app;

    private CopyOnWriteArrayList<Long> costList = new CopyOnWriteArrayList<>();

    public void increaseInvokeTimes() {
        invokeTimes.incrementAndGet();
    }

    public void increaseFailureTimes() {
        invokeFailureTimes.incrementAndGet();
    }

    public void increaseTotalCost(long cost) {
        invokeTotalCost.addAndGet(cost);
    }

    public void addInvocationCost(long cost) {
        costList.add(cost);
    }

    public void populateMaxAndMinCost() {
        this.invokeMaxCost = Collections.max(costList);
        this.invokeMinCost = Collections.min(costList);
    }
}

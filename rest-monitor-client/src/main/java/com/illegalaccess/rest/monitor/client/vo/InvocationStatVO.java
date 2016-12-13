package com.illegalaccess.rest.monitor.client.vo;

import lombok.Data;

/**
 * Created by Administrator on 2016/12/13.
 */
@Data
public class InvocationStatVO {
    //总调用次数
    private long invokeTimes;
    //调用失败次数
    private long invokeFailureTimes;
    //总耗时
    private long invokeTotalCost;
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
}

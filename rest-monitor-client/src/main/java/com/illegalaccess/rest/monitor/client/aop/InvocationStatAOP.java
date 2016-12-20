package com.illegalaccess.rest.monitor.client.aop;

import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/12/20.
 */
public class InvocationStatAOP {

    private String app;
    private String ip;

    public void setApp(String app) {
        this.app = app;
    }

    public InvocationStatAOP() {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计正常情况方法调用次数,耗时
     * @param j
     */
    public void statGeneralInvocationData(JoinPoint j) {
        InvocationStatVO stat = StatContainer.Instance.getStat(j.getSignature().getName());
        if (stat == null) {
            InvocationStatVO set = StatContainer.Instance.putStat(j.getSignature().getName(), stat);
            if (set != null) { //other thread set value
                stat = StatContainer.Instance.getStat(j.getSignature().getName());
            }
        }
        stat.increaseInvokeTimes();
        long start = System.currentTimeMillis();
        try {
            ((ProceedingJoinPoint) j).proceed();
            long cost = System.currentTimeMillis() - start;
            stat.increaseTotalCost(cost);
            stat.addInvocationCost(cost);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            long cost = System.currentTimeMillis() - start;
            stat.increaseTotalCost(cost);
            stat.addInvocationCost(cost);
        }

    }

    /**
     * 统计调用失败的次数
     * @param j
     */
    public void statFailureInvocationData(JoinPoint j) {
        InvocationStatVO stat = StatContainer.Instance.getStat(j.getSignature().getName());
        if (stat == null) {
            InvocationStatVO set = StatContainer.Instance.putStat(j.getSignature().getName(), stat);
            if (set != null) { //other thread set value
                stat = StatContainer.Instance.getStat(j.getSignature().getName());
            }
        }
        stat.increaseFailureTimes();
    }
}

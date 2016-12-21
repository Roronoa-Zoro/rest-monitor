package com.illegalaccess.rest.monitor.client.aop;

import com.illegalaccess.rest.monitor.client.support.StatContainer;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Administrator on 2016/12/20.
 */
public class InvocationStatAOP {

    private String app;
    private String ip;
    private List<Class<? extends Throwable>> includedException = new ArrayList<>();
    private List<String> includedExceptionNames = new ArrayList<>();
    private List<Class<? extends Throwable>> excludedException = new ArrayList<>();
    private List<String> excludedExceptionName = new ArrayList<>();

    public void setApp(String app) {
        this.app = app;
    }

    public void setIncludedException(List<Class<? extends Throwable>> includedException) {
        this.includedException = includedException;
        includedException.stream().forEach(ie -> includedExceptionNames.add(ie.getName()));
    }

    public void setExcludedException(List<Class<? extends Throwable>> excludedException) {
        this.excludedException = excludedException;
        excludedException.stream().forEach(ee -> excludedExceptionName.add(ee.getName()));
    }

    public void setIncludedExceptionNames(List<String> includedExceptionNames) {
        this.includedExceptionNames = includedExceptionNames;
    }

    public void setExcludedExceptionName(List<String> excludedExceptionName) {
        this.excludedExceptionName = excludedExceptionName;
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
    public void statFailureInvocationData(JoinPoint j, Throwable ex) {
        InvocationStatVO stat = StatContainer.Instance.getStat(j.getSignature().getName());
        if (stat == null) {
            InvocationStatVO set = StatContainer.Instance.putStat(j.getSignature().getName(), stat);
            if (set != null) { //other thread set value
                stat = StatContainer.Instance.getStat(j.getSignature().getName());
            }
        }

        Optional<String> found = includedExceptionNames.stream()
                .filter(ien -> ien.equals(ex.getClass().getName()))
                .findFirst();
        if (found.isPresent()) {
            stat.increaseFailureTimes();
            return;
        }

        found = excludedExceptionName.stream()
                .filter(een -> een.equals(ex.getClass().getName()))
                .findFirst();
        if (found.isPresent()) {
            return;
        }
        stat.increaseFailureTimes();
    }
}

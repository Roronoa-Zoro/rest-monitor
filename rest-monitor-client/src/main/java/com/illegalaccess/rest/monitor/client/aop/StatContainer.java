package com.illegalaccess.rest.monitor.client.aop;

import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2016/12/20.
 */
public enum StatContainer {
    Instance;

    private ConcurrentMap<String, InvocationStatVO> container = new ConcurrentHashMap<>();

    public InvocationStatVO getStat(String methodName) {
        return container.get(methodName);
    }

    public InvocationStatVO putStat(String methodName, InvocationStatVO statVO) {
        return container.putIfAbsent(methodName, statVO);
    }

    public void clear() {
        container = new ConcurrentHashMap<>();
    }

    public Collection<InvocationStatVO> getCurrentData() {
        return container.values();
    }
}

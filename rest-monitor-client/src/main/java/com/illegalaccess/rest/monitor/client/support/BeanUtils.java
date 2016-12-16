package com.illegalaccess.rest.monitor.client.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/12/16.
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext ac;
    private final ReentrantLock lock = new ReentrantLock();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            lock.lock();
            BeanUtils.ac = applicationContext;
        } finally {
            lock.unlock();
        }
    }

    public static final <T> T getBean(Class<T> clazz) {
        return ac.getBean(clazz);
    }

    public static final <T> T getBean(String name, Class<T> clazz) {
        return ac.getBean(name, clazz);
    }
}

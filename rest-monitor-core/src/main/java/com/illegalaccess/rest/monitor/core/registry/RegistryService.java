package com.illegalaccess.rest.monitor.core.registry;

/**
 * Created by Administrator on 2016/12/19.
 * 注册服务
 */
public interface RegistryService {

    /**
     * 向服务中心注册本机ip port
     */
    void registerService();
}

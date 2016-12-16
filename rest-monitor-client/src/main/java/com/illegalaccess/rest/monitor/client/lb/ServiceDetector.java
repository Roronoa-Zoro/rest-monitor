package com.illegalaccess.rest.monitor.client.lb;

/**
 * Created by Administrator on 2016/12/15.
 * 远程服务发现
 */
public interface ServiceDetector {

    RegisterHost discoverRemoteHost();
}

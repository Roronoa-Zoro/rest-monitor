package com.illegalaccess.rest.monitor.client.remoting;

import com.illegalaccess.rest.monitor.client.lb.RegisterHost;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;

import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 * 链接到远程服务的客户端
 */
public interface RemoteClient {

    void startClient(RegisterHost registerHost);

    void heartbeat();

    void reConnect();

    void sendData(List<InvocationStatVO> statData);
}

package com.illegalaccess.rest.monitor.client.lb;

import lombok.Data;

/**
 * Created by Administrator on 2016/12/15.
 * 远程主机信息
 */
@Data
public class RegisterHost {

    private String remoteHostIP;
    private int remoteHostPort;
}

package com.illegalaccess.rest.monitor.core.vo;

import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */
@Data
public class ReportPoint {

    private List<InvocationStatListProto.InvocationStatListVO> statList;
    //多少个point写入db
    private int pointLimit;
    //间隔多久写入db
    private LocalDateTime receivedTime;
}

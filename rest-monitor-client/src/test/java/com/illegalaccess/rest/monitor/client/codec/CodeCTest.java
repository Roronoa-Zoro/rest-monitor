package com.illegalaccess.rest.monitor.client.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import org.junit.Test;

/**
 * Created by Administrator on 2016/12/16.
 */
public class CodeCTest {

    @Test
    public void protobufListTest() throws InvalidProtocolBufferException {
        InvocationStatListProto.InvocationStatListVO.Builder builder =
                InvocationStatListProto.InvocationStatListVO.newBuilder();
        InvocationStatListProto.InvocationStatVO.Builder voBuilder =
                InvocationStatListProto.InvocationStatVO.newBuilder();
        for (int i = 0; i < 10; i++) {
            voBuilder.setApp("Insurance");
            voBuilder.setHost("localhost");
            voBuilder.setMethodName("sayHello");
            voBuilder.setInvokeFailureTimes(1L);
            voBuilder.setInvokeMaxCost(100L);
            voBuilder.setInvokeMinCost(20L);
            voBuilder.setInvokeTimes(20L);
            voBuilder.setInvokeTotalCost(200L);
            voBuilder.setReportTimestamp(1000000L);
            builder.addStatVOList(voBuilder.build());
            voBuilder.clear();
        }
        InvocationStatListProto.InvocationStatListVO result = builder.build();
        System.out.println(result);
//        builder.addStatVOList()
    }

    @Test
    public void protobufTest() throws InvalidProtocolBufferException {
//        InvocationStatProto.InvocationStatVO statVO = prepareData();
//        System.out.println(statVO);
//        System.out.println(InvocationStatProto.InvocationStatVO.parseFrom(statVO.toByteArray()));
    }

//    private InvocationStatProto.InvocationStatVO prepareData() {
//        InvocationStatProto.InvocationStatVO.Builder builder = InvocationStatProto.InvocationStatVO.newBuilder();
//        builder.setApp("Insurance");
//        builder.setHost("localhost");
//        builder.setMethodName("sayHello");
//        builder.setInvokeFailureTimes(1L);
//        builder.setInvokeMaxCost(100L);
//        builder.setInvokeMinCost(20L);
//        builder.setInvokeTimes(20L);
//        builder.setInvokeTotalCost(200L);
//        builder.setReportTimestamp(1000000L);
//        return builder.build();
//    }
}

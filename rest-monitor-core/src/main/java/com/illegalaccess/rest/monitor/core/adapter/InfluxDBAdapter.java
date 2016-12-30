package com.illegalaccess.rest.monitor.core.adapter;

import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jimmy Li on 2016/12/28.
 */
@Slf4j
@Service
public class InfluxDBAdapter {
    @Autowired
    private InfluxDB influxDB;
    @Value("${database}")
    private String database;
    @Value("${retentionPolicy}")
    private String retentionPolicy;
    @Value("${table}")
    private String table;

    public void singleWrite(InvocationStatListProto.InvocationStatVO data) {

        log.info("will save {} 1 rows", table);
        influxDB.write(table, retentionPolicy, convertToPoint(data));
    }

    public void batchWrite(List<InvocationStatListProto.InvocationStatVO> data) {
        influxDB.write(convertToBatchPoints(data));
    }

    private Point convertToPoint(InvocationStatListProto.InvocationStatVO data) {
        return convertToPoint(data, Point.measurement(table));
    }

    private Point convertToPoint(InvocationStatListProto.InvocationStatVO data, Point.Builder pointBuilder) {
        pointBuilder.addField("methodName", data.getMethodName());
        pointBuilder.addField("totalCost", data.getInvokeTotalCost());
        pointBuilder.addField("invokeTimes", data.getInvokeTimes());
        pointBuilder.addField("invokeFailureTimes", data.getInvokeFailureTimes());
        pointBuilder.addField("maxCost", data.getInvokeMaxCost());
        pointBuilder.addField("minCost", data.getInvokeMinCost());
        pointBuilder.time(data.getReportTimestamp(), TimeUnit.NANOSECONDS);

        pointBuilder.tag("app", data.getApp());
        pointBuilder.tag("host", data.getHost());
        return pointBuilder.build();
    }

    private BatchPoints convertToBatchPoints(List<InvocationStatListProto.InvocationStatVO> data) {
        BatchPoints.Builder builder = BatchPoints.database(database);
        builder.retentionPolicy(retentionPolicy);
        Point[] points = new Point[data.size()];
        Point.Builder pointBuilder = Point.measurement(table);
        for (int i = 0; i < points.length; i++) {
            points[i] = convertToPoint(data.get(i), pointBuilder);
        }
        builder.points(points);
        return builder.build();
    }
}

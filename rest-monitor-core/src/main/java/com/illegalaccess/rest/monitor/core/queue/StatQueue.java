package com.illegalaccess.rest.monitor.core.queue;

import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jimmy Li on 2016/12/28.
 */
@Slf4j
@Component
public class StatQueue {

    private BlockingQueue<InvocationStatListProto.InvocationStatListVO> queue;
    private String filePath = "D:\\business\\tmp\\test.txt";

    @PostConstruct
    public void init() throws Exception {
        queue = new LinkedBlockingQueue<>();
        if (Paths.get(filePath).toFile().exists()) {
            InvocationStatListProto.InvocationStatListVO total = InvocationStatListProto.InvocationStatListVO.parseFrom(Files.newInputStream(Paths.get("D:\\business\\tmp\\test.txt"), StandardOpenOption.READ));
            queue.add(total);
        }
        Paths.get(filePath).toFile().delete();
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (queue.isEmpty()) {
            return;
        }
        log.info("there is non processed data, save to local dist");
        InvocationStatListProto.InvocationStatListVO[] nonProcessed = queue.toArray(new InvocationStatListProto.InvocationStatListVO[0]);
        for (InvocationStatListProto.InvocationStatListVO data : nonProcessed) {
            Files.write(Paths.get(filePath), data.toByteArray(), StandardOpenOption.APPEND);
        }
    }

    public void putMessage(InvocationStatListProto.InvocationStatListVO msg) throws InterruptedException {
        queue.put(msg);
    }

    public boolean offerMessage(InvocationStatListProto.InvocationStatListVO msg) {
        return queue.offer(msg);
    }

    public InvocationStatListProto.InvocationStatListVO takeMessage() throws InterruptedException {
        return queue.take();
    }

    public InvocationStatListProto.InvocationStatListVO pollMessage() {
        return queue.poll();
    }

    public InvocationStatListProto.InvocationStatListVO pollMessage(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(timeout, timeUnit);
    }
}

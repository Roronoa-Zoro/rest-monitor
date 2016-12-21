package com.illegalaccess.rest.monitor.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/21.
 */
@RestController
@RequestMapping("/sampleController")
public class SampleController {

    @RequestMapping(value = "/doSampleLogic", method = RequestMethod.GET)
    public String doSampleLogic() {
        System.out.println(Thread.currentThread().getName() + " do sample logic");
        try {
            Random r = new Random();
            TimeUnit.MILLISECONDS.sleep(r.nextInt(300));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
